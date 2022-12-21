package io.space.serverlist;

import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;

public final class ServerListManager {
    public static ServerListManager Instance;

    private final LinkedHashMap<String,ServerInfo> serverMap = new LinkedHashMap<>();

    @SuppressWarnings("SpellCheckingInspection")
    public void init() {
        serverMap.put("Hypixel",new ServerInfo(new ResourceLocation("textures/servericon/hypixel.png"),"Hypixel","mc.hypixel.net","全世界最大的Minecraft服务器"));
        serverMap.put("Syuu",new ServerInfo(new ResourceLocation("textures/servericon/syuu.png"),"Syuu","syuu.net","没有介绍!"));
        serverMap.put("FunCraft",new ServerInfo(new ResourceLocation("textures/servericon/funcraft.png"),"FunCraft","funcraft.net","没有介绍!"));
        serverMap.put("BlocksMC",new ServerInfo(new ResourceLocation("textures/servericon/blocksmc.png"),"BlocksMC","blocksmc.com","没有介绍!"));
        serverMap.put("HmXix",new ServerInfo(new ResourceLocation("textures/servericon/hmxix.png"),"HmXix","Play.HmXix.Top","黑客服务器"));
        serverMap.put("LoyisaTestServer",new ServerInfo(new ResourceLocation("textures/servericon/loyisa.png"),"Loyisa Test Server","mc.loyisa.cn","反作弊测试服务器"));
        serverMap.put("MineMora",new ServerInfo(new ResourceLocation("textures/servericon/minemora.png"),"MineMora","mc.minemora.net","没有介绍!"));
        serverMap.put("MinePlex",new ServerInfo(new ResourceLocation("textures/servericon/mineplex.png"),"MinePlex","us.mineplex.com","没有介绍!"));
        serverMap.put("MineLand",new ServerInfo(new ResourceLocation("textures/servericon/mineland.png"),"MineLand","mc.mineland.me","没有介绍!"));
        serverMap.put("LuckyNetwork",new ServerInfo(new ResourceLocation("textures/servericon/luckynetwork.png"),"LuckyNetwork","luckynetwork.net","没有介绍!"));
        serverMap.put("JartexNetwork",new ServerInfo(new ResourceLocation("textures/servericon/jartex.png"),"JartexNetwork","jartexnetwork.com","没有介绍!"));
    }

    public LinkedHashMap<String, ServerInfo> getServerMap() {
        return serverMap;
    }
}
