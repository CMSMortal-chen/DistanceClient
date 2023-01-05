package net.minecraft.client;

import my.distance.util.ClientSetting;

public class ClientBrandRetriever
{
    public static String getClientModName()
    {
        return ClientSetting.fakeForge.getValue() ? "fml,forge" : "vanilla";
    }
}
