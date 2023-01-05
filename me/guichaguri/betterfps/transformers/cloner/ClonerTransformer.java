package me.guichaguri.betterfps.transformers.cloner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import me.guichaguri.betterfps.ASMUtils;
import me.guichaguri.betterfps.BetterFps;
import me.guichaguri.betterfps.BetterFpsConfig;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.transformers.cloner.CopyMode.Mode;
import me.guichaguri.betterfps.tweaker.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

/**
 * This class will clone .class methods/fields to the real class
 * @author Guilherme Chaguri
 */
public class ClonerTransformer implements IClassTransformer {

    private static final List<Clone> clones = new ArrayList<>();

    public static void add(String clazz, Naming target) {
        clones.add(new Clone(clazz.replaceAll("\\.", "/"), target));
    }

    static {
        BetterFpsConfig config = BetterFpsConfig.getConfig();

        if(config.fastBeacon) {
            add("me.guichaguri.betterfps.clones.tileentity.BeaconLogic", Naming.C_TileEntityBeacon);
        }

        if(config.fastHopper) {
            add("me.guichaguri.betterfps.clones.tileentity.HopperLogic", Naming.C_TileEntityHopper);
            add("me.guichaguri.betterfps.clones.block.HopperBlock", Naming.C_BlockHopper);
        }

        add("me.guichaguri.betterfps.clones.client.ModelBoxLogic", Naming.C_ModelBox);
        add("me.guichaguri.betterfps.clones.client.EntityRenderLogic", Naming.C_EntityRenderer);
        add("me.guichaguri.betterfps.clones.client.GuiOptionsLogic", Naming.C_GuiOptions);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return bytes;

        List<Clone> foundClones = null;
        for(Clone c : clones) {
            if(c.target.is(name)) {
                if(foundClones == null) foundClones = new ArrayList<>();
                foundClones.add(c);
            }
        }

        if(foundClones != null) {
            BetterFps.log.info("Found " + foundClones.size() + " class patches for " + name);
            return patchClones(foundClones, bytes);
        }

        return bytes;
    }

    public byte[] patchClones(List<Clone> clones, byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean patched = false;
        ClassReader reader;

        for(Clone c : clones) {

            try {
                if(BetterFpsHelper.LOC == null) { // Development or vanilla environment?
                    reader = new ClassReader(c.clonePath);
                } else { // Forge environment
                    JarFile jar = new JarFile(BetterFpsHelper.LOC);
                    ZipEntry e = jar.getEntry(c.clonePath + ".class");
                    reader = new ClassReader(jar.getInputStream(e));
                    jar.close();
                }

                ClassNode cloneClass = new ClassNode();
                reader.accept(cloneClass, 0);

                if(cloneClass.visibleAnnotations != null) {
                    boolean canCopy = canCopy(cloneClass.visibleAnnotations);
                    if(!canCopy) continue;
                }

                fields: for(FieldNode field : cloneClass.fields) {
                    CopyMode.Mode mode = Mode.REPLACE;
                    Naming name = null;
                    if(field.visibleAnnotations != null) {
                        boolean canCopy = canCopy(field.visibleAnnotations);
                        if(!canCopy) continue fields;
                        mode = getCopyMode(field.visibleAnnotations);
                        name = getNaming(field.visibleAnnotations);
                    }
                    cloneField(field, classNode, mode, name);
                    patched = true;
                }

                methods: for(MethodNode method : cloneClass.methods) {
                    CopyMode.Mode mode = Mode.REPLACE;
                    Naming name = null;
                    if(method.visibleAnnotations != null) {
                        boolean canCopy = canCopy(method.visibleAnnotations);
                        if(!canCopy) continue methods;
                        mode = getCopyMode(method.visibleAnnotations);
                        name = getNaming(method.visibleAnnotations);
                    }
                    cloneMethod(method, classNode, cloneClass, mode, name);
                    patched = true;
                }

            } catch(Exception ex) {
                BetterFps.log.error("Could not patch with " + c.clonePath + ": " + ex);
                ex.printStackTrace();
            }

        }

        if(!patched) return bytes;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private Mode getCopyMode(List<AnnotationNode> annotations) {
        for(AnnotationNode node : annotations) {
            if(node.desc.equals(Type.getDescriptor(CopyMode.class))) {
                Mode n = ASMUtils.getAnnotationValue(node, "value", Mode.class);
                if(n != null) return n;
            }
        }
        return Mode.REPLACE;
    }

    private Naming getNaming(List<AnnotationNode> annotations) {
        for(AnnotationNode node : annotations) {
            if(node.desc.equals(Type.getDescriptor(Named.class))) {
                Naming n = ASMUtils.getAnnotationValue(node, "value", Naming.class);
                if(n != null) return n;
            }
        }
        return null;
    }

    private boolean canCopy(List<AnnotationNode> annotations) {
        boolean canCopy = false;
        int conditions = 0;
        for(AnnotationNode node : annotations) {
            if(node.desc.equals(Type.getDescriptor(CopyCondition.class))) {
                String key = ASMUtils.getAnnotationValue(node, "key");
                String value = ASMUtils.getAnnotationValue(node, "value");
                canCopy = canCopy || BetterFpsConfig.getValue(key).equals(value);
                conditions++;
            } else if(node.desc.equals(Type.getDescriptor(CopyBoolCondition.class))) {
                String key = ASMUtils.getAnnotationValue(node, "key");
                Boolean value = ASMUtils.getAnnotationValue(node, "value", Boolean.class);
                if(value == null) value = true;
                canCopy = canCopy || (boolean)(Boolean)BetterFpsConfig.getValue(key) == (boolean)value;
                conditions++;
            }
        }
        return conditions > 0 ? canCopy : true;
    }


    private void cloneField(FieldNode e, ClassNode node, Mode mode, Naming name) {
        if(mode == Mode.IGNORE) return;
        for(int i = 0; i < node.fields.size(); i++) {
            FieldNode field = node.fields.get(i);
            boolean b = false;
            if((name != null) && (name.is(field.name, field.desc))) {
                b = true;
                e.name = field.name;
                e.desc = field.desc;
            } else if((field.name.equals(e.name)) && (field.desc.equals(e.desc))) {
                b = true;
            }
            if(b) {
                if(mode == Mode.ADD_IF_NOT_EXISTS) return;
                node.fields.remove(field);
                break;
            }
        }
        node.fields.add(e);
    }

    private boolean cloneMethod(MethodNode e, ClassNode node, ClassNode original, Mode mode, Naming name) {
        if(mode == Mode.IGNORE) return false;
        MethodNode originalMethod = null;
        for(int i = 0; i < node.methods.size(); i++) {
            MethodNode method = node.methods.get(i);
            boolean b = false;
            if((name != null) && (name.is(method.name)) && (method.desc.equals(e.desc))) {
                b = true;
                e.name = method.name;
                e.desc = method.desc;
            } else if((method.name.equals(e.name)) && (method.desc.equals(e.desc))) {
                b = true;
            }
            if(b) {
                if(mode == Mode.ADD_IF_NOT_EXISTS) return false;
                if(mode == Mode.PREPEND) {
                    replaceOcurrences(e, node, original, null);
                    method.instructions = ASMUtils.prependNodeList(method.instructions, e.instructions);
                    return true;
                }
                if(mode == Mode.APPEND) {
                    replaceOcurrences(e, node, original, null);
                    method.instructions = ASMUtils.appendNodeList(method.instructions, e.instructions);
                    return true;
                }
                originalMethod = method;
                node.methods.remove(method);
                break;
            }
        }
        replaceOcurrences(e, node, original, originalMethod);
        node.methods.add(e);
        return true;
    }

    private void replaceOcurrences(MethodNode method, ClassNode classNode, ClassNode original, MethodNode originalMethod) {
        String originalDesc = "L" + original.name + ";";
        String classDesc = "L" + classNode.name + ";";
        Iterator<AbstractInsnNode> nodes = method.instructions.iterator();
        TypeInsnNode lastType = null;
        boolean hasSuper = false;
        String superName = originalMethod == null ? null : originalMethod.name;
        nodeLoop: while(nodes.hasNext()) {
            AbstractInsnNode node = nodes.next();

            if(node instanceof FieldInsnNode) {
                FieldInsnNode f = (FieldInsnNode)node;
                if(f.owner.equals(original.name)) {
                    f.owner = classNode.name;
                } else {
                    for(Clone c : clones) {
                        if(f.owner.equals(c.clonePath)) {
                            f.owner = lastType.desc;
                            continue nodeLoop;
                        }
                    }
                }
            } else if(node instanceof MethodInsnNode) {
                MethodInsnNode m = (MethodInsnNode)node;

                if(originalMethod != null && m.getOpcode() == Opcodes.INVOKESPECIAL && m.owner.equals(classNode.name)
                        && m.name.equals(superName) && m.desc.equals(originalMethod.desc)) {
                    if(!hasSuper) {
                        originalMethod.name = originalMethod.name + "_BF_repl";
                        classNode.methods.add(originalMethod);
                        hasSuper = true;
                    }
                    m.setOpcode(Opcodes.INVOKEVIRTUAL);
                    m.name = originalMethod.name;
                }

                if(m.owner.equals(original.name)) {
                    m.owner = classNode.name;
                } else {
                    for(Clone c : clones) {
                        if(m.owner.equals(c.clonePath)) {
                            m.owner = lastType.desc;
                            continue nodeLoop;
                        }
                    }
                }
            } else if(node instanceof TypeInsnNode) {
                TypeInsnNode t = (TypeInsnNode)node;
                if(t.desc.equals(original.name)) {
                    t.desc = classNode.name;
                } else {
                    for(Clone c : clones) {
                        if(t.desc.equals(c.clonePath)) {
                            nodes.remove();
                            continue nodeLoop;
                        }
                    }
                    lastType = t;
                }
            }
        }
        for(LocalVariableNode var : method.localVariables) {
            if(var.desc == originalDesc) {
                var.desc = classDesc;
            }
        }
    }

    public static class Clone {
        public final String clonePath;
        public final Naming target;

        public Clone(String clonePath, Naming target) {
            this.clonePath = clonePath;
            this.target = target;
        }
    }

}
