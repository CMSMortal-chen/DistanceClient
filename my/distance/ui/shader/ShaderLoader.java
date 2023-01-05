package my.distance.ui.shader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import my.distance.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

public class ShaderLoader {
    private int vertexShaderId;
    private int fragmentShaderId;
    private int programmId;
    private int fboTextureID;
    private int fboID;
    private int renderBufferID;
    private String vertexShaderFileName;
    private String fragmenShaderFileName;
    private int resolutionUniformId;
    private int timeUniformID;
    private int mouseUniformId;
    private int texelUniformId;
    private int frameBufferTextureId;
    private int diffuseSamperUniformID;
    private float time = 0.0F;

    public ShaderLoader(String fragmentShader, int frameBufferTextureId) {
        this.reset();
        this.vertexShaderFileName = "vertex.shader";
        this.fragmenShaderFileName = fragmentShader;
        this.frameBufferTextureId = frameBufferTextureId;
        this.generateFBO();
        this.initShaders();
    }

    private void generateFBO() {
        this.fboID = EXTFramebufferObject.glGenFramebuffersEXT();
        this.fboTextureID = GL11.glGenTextures();
        this.renderBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        GL11.glBindTexture(3553, this.fboTextureID);
        GL11.glTexParameterf(3553, 10241, 9729.0F);
        GL11.glTexParameterf(3553, 10240, 9729.0F);
        GL11.glTexParameterf(3553, 10242, 10496.0F);
        GL11.glTexParameterf(3553, 10243, 10496.0F);
        GL11.glBindTexture(3553, 0);
        GL11.glBindTexture(3553, this.fboTextureID);
        GL11.glTexImage2D(3553, 0, 32856, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, 0, 6408, 5121, (ByteBuffer)null);
        EXTFramebufferObject.glBindFramebufferEXT(36160, this.fboID);
        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, this.fboTextureID, 0);
        EXTFramebufferObject.glBindRenderbufferEXT(36161, this.renderBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, this.renderBufferID);
    }

    public int getFboTextureID() {
        return this.fboTextureID;
    }

    public void initShaders() {
        if (this.programmId == -1) {
            this.programmId = ARBShaderObjects.glCreateProgramObjectARB();

            try {
                InputStream in;
                String fragmentShader;
                if (this.vertexShaderId == -1) {
                    in = Minecraft.getMinecraft().getResourceManager()
                            .getResource(new ResourceLocation("Distance/shader/"+this.vertexShaderFileName)).getInputStream();
                    fragmentShader = RenderUtil.getShaderCode(new InputStreamReader(in));
                    this.vertexShaderId = RenderUtil.createShader(fragmentShader, ARBVertexShader.GL_VERTEX_SHADER_ARB);
                }

                if (this.fragmentShaderId == -1) {
                    in = Minecraft.getMinecraft().getResourceManager()
                            .getResource(new ResourceLocation("Distance/shader/fragment/"+this.fragmenShaderFileName)).getInputStream();
                    fragmentShader = RenderUtil.getShaderCode(new InputStreamReader(in));
                    this.fragmentShaderId = RenderUtil.createShader(fragmentShader, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
                }
            } catch (Exception var3) {
                this.programmId = -1;
                this.vertexShaderId = -1;
                this.fragmentShaderId = -1;
                var3.printStackTrace();
            }

            if (this.programmId != -1) {
                ARBShaderObjects.glAttachObjectARB(this.programmId, this.vertexShaderId);
                ARBShaderObjects.glAttachObjectARB(this.programmId, this.fragmentShaderId);
                ARBShaderObjects.glLinkProgramARB(this.programmId);
                if (ARBShaderObjects.glGetObjectParameteriARB(this.programmId, 35714) == 0) {
                    System.err.println(this.programmId);
                    return;
                }

                ARBShaderObjects.glValidateProgramARB(this.programmId);
                if (ARBShaderObjects.glGetObjectParameteriARB(this.programmId, 35715) == 0) {
                    System.err.println(this.programmId);
                    return;
                }

                ARBShaderObjects.glUseProgramObjectARB(0);
                this.resolutionUniformId = ARBShaderObjects.glGetUniformLocationARB(this.programmId, "resolution");
                this.timeUniformID = ARBShaderObjects.glGetUniformLocationARB(this.programmId, "timeHelper");
                this.mouseUniformId = ARBShaderObjects.glGetUniformLocationARB(this.programmId, "mouse");
                this.diffuseSamperUniformID = ARBShaderObjects.glGetUniformLocationARB(this.programmId, "diffuseSamper");
                this.texelUniformId = ARBShaderObjects.glGetUniformLocationARB(this.programmId, "texel");
            }
        }

    }
    private long lastFrame = System.currentTimeMillis();

    public ShaderLoader update() {
        if (this.fboID != -1 && this.renderBufferID != -1 && this.programmId != -1) {
            EXTFramebufferObject.glBindFramebufferEXT(36160, this.fboID);
            GL11.glClear(16640);
            ARBShaderObjects.glUseProgramObjectARB(this.programmId);
            ARBShaderObjects.glUniform1iARB(this.diffuseSamperUniformID, 0);
            GL13.glActiveTexture(33984);
            GL11.glEnable(3553);
            GL11.glBindTexture(3553, this.frameBufferTextureId);
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            FloatBuffer resolutionBuffer = BufferUtils.createFloatBuffer(2);
            resolutionBuffer.position(0);
            resolutionBuffer.put((float)(Minecraft.getMinecraft().displayWidth));
            resolutionBuffer.put((float)(Minecraft.getMinecraft().displayHeight));
            resolutionBuffer.flip();
            ARBShaderObjects.glUniform2ARB(this.resolutionUniformId, resolutionBuffer);
            FloatBuffer texelSizeBuffer = BufferUtils.createFloatBuffer(2);
            texelSizeBuffer.position(0);
            texelSizeBuffer.put(1.0F / (float)Minecraft.getMinecraft().displayWidth * 2.0F);
            texelSizeBuffer.put(1.0F / (float)Minecraft.getMinecraft().displayHeight * 2.0F);
            texelSizeBuffer.flip();
            ARBShaderObjects.glUniform2ARB(this.texelUniformId, texelSizeBuffer);
            float mouseX = (float)Mouse.getX() / (float)Minecraft.getMinecraft().displayWidth;
            float mouseY = (float)Mouse.getY() / (float)Minecraft.getMinecraft().displayHeight;
            FloatBuffer mouseBuffer = BufferUtils.createFloatBuffer(2);
            mouseBuffer.position(0);
            mouseBuffer.put(mouseX);
            mouseBuffer.put(mouseY);
            mouseBuffer.flip();
            ARBShaderObjects.glUniform2ARB(this.mouseUniformId, mouseBuffer);
            long thisFrame = System.currentTimeMillis();
            time += (thisFrame - this.lastFrame) / 700f;
            this.lastFrame = thisFrame;
            ARBShaderObjects.glUniform1fARB(this.timeUniformID, this.time);
            double width = res.getScaledWidth();
            double height = res.getScaledHeight();
            GL11.glDisable(3553);
            GL11.glBegin(4);
            GL11.glTexCoord2d(0.0D, 1.0D);
            GL11.glVertex2d(0.0D, 0.0D);
            GL11.glTexCoord2d(0.0D, 0.0D);
            GL11.glVertex2d(0.0D, height);
            GL11.glTexCoord2d(1.0D, 0.0D);
            GL11.glVertex2d(width, height);
            GL11.glTexCoord2d(1.0D, 0.0D);
            GL11.glVertex2d(width, height );
            GL11.glTexCoord2d(1.0D, 1.0D);
            GL11.glVertex2d(width, 0.0D);
            GL11.glTexCoord2d(0.0D, 1.0D);
            GL11.glVertex2d(0.0D, 0.0D);
            GL11.glEnd();
            ARBShaderObjects.glUseProgramObjectARB(0);
            return this;
        } else {
            throw new RuntimeException("Invalid IDs!");
        }
    }

    private void reset() {
        this.vertexShaderId = -1;
        this.fragmentShaderId = -1;
        this.programmId = -1;
        this.fboTextureID = -1;
        this.fboID = -1;
        this.renderBufferID = -1;
    }

    public void delete() {
        if (this.renderBufferID > -1) {
            EXTFramebufferObject.glDeleteRenderbuffersEXT(this.renderBufferID);
        }

        if (this.fboID > -1) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(this.fboID);
        }

        if (this.fboTextureID > -1) {
            GL11.glDeleteTextures(this.fboTextureID);
        }

    }
}

