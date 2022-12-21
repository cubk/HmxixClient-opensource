package io.space.designer.designerimpl;

import io.space.Wrapper;
import io.space.designer.Designer;
import io.space.mod.visual.PacketGraph;
import io.space.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import utils.hodgepodge.object.time.TimerUtils;

import java.util.LinkedList;

public final class PacketGraphDesigner extends Designer {
    public static PacketGraphDesigner Instance;

    private final TimerUtils updateTimerUtils = new TimerUtils(true);
    private final TimerUtils secTimerUtils = new TimerUtils(true);

    private final LinkedList<Vertex> clientVertexList = new LinkedList<>();
    private final LinkedList<Vertex> serverVertexList = new LinkedList<>();

    private int clientPackets,serverPackets,secClientPackets,secServerPackets,renderSecClientPackets,renderSecServerPackets;

    public PacketGraphDesigner() {
        super(Type.PACKET_GRAPH);
        x = 2;
        y = 60;

        Instance = this;
    }

    public void onPacket(boolean client) {
        if (client) {
            clientPackets++;
            secClientPackets++;
        }
        else {
            serverPackets++;
            secServerPackets++;
        }
    }

    @Override
    public void draw(float partialTicks, int mouseX, int mouseY) {
        if (updateTimerUtils.hasReached(PacketGraph.updateTime.getValue())) {
            for (Vertex vertex : clientVertexList) {
                vertex.x++;
            }

            clientVertexList.add(new Vertex(0,Math.min(clientPackets,43)));
            clientPackets = 0;

            for (Vertex vertex : serverVertexList) {
                vertex.x++;
            }

            serverVertexList.add(new Vertex(0,Math.min(serverPackets,43)));
            serverPackets = 0;
        }

        if (secTimerUtils.hasReached(1000)) {
            renderSecClientPackets = secClientPackets;
            renderSecServerPackets = secServerPackets;
            secClientPackets = 0;
            secServerPackets = 0;
        }

        double graphY = y;

        for (int i = 0; i < 2; i++) {
            final boolean isClientSide = i == 0;

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5,0.5,0.5);
            final String secString = (isClientSide ? renderSecClientPackets : renderSecServerPackets) + " packets / sec";
            mc.customFontRenderer.drawStringWithOutline(isClientSide ? "Outgoing packets" : "Incoming packets",(float) x * 2,(float) graphY * 2,-1);
            mc.customFontRenderer.drawStringWithOutline(secString,(float) (x + 120) * 2 - mc.customFontRenderer.getStringWidth(secString),(float) graphY * 2,-1);
            GlStateManager.popMatrix();

            RenderUtils.drawRect(x,graphY + 5,x + 120,graphY + 50,RenderUtils.getRGB(0,0,0,30));

            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glLineWidth(1.5f);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glBegin(GL11.GL_LINES);
            RenderUtils.glColor(PacketGraph.color.getValue());
            LinkedList<Vertex> list = isClientSide ? clientVertexList : serverVertexList;
            for (Vertex vertex : list) {
                try {
                    final Vertex nextVertex = list.get(list.indexOf(vertex) + 1);

                    if (nextVertex != null) {
                        GL11.glVertex2d(x + vertex.x, graphY + 48 - vertex.height);
                        GL11.glVertex2d(x + vertex.x - 1, graphY + 48 - nextVertex.height);
                    }
                } catch (Exception ignored) {}
            }
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GlStateManager.resetColor();

            graphY += 51;
        }

        clientVertexList.removeIf(vertex -> vertex.x > 120);
        serverVertexList.removeIf(vertex -> vertex.x > 120);
    }

    @Override
    public boolean canDrag (int mouseX, int mouseY) {
        return Wrapper.isHovered(x,y,x + 120,y + 100,mouseX,mouseY) && Mouse.isButtonDown(0);
    }

    private static class Vertex {
        public double x;
        public final double height;

        public Vertex(double x, double height) {
            this.x = x;
            this.height = height;
        }
    }
}
