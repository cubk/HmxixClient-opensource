package net.minecraft.client.renderer.entity;

import java.util.Random;

import io.space.mod.visual.ItemPhysics;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEntityItem extends Render<EntityItem>
{
    private final RenderItem itemRenderer;
    private Random field_177079_e = new Random();

    public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_)
    {
        super(renderManagerIn);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_,boolean physics,float yOffset)
    {
        ItemStack itemstack = itemIn.getEntityItem();
        Item item = itemstack.getItem();

        if (item == null)
        {
            return 0;
        }
        else
        {
            boolean flag = p_177077_9_.isGui3d();
            int i = this.func_177078_a(itemstack);

            float f1 = MathHelper.sin((physics ? 0 : ((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart)) * 0.1F + 0.1F;
            float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + yOffset * f2, (float)p_177077_6_);

            if (flag || this.renderManager.options != null)
            {
                float f3 = (((physics ? 0 : (float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart)) * (180F / (float)Math.PI);
                GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }

            if (!flag)
            {
                float f6 = -0.0F * (float)(i - 1) * 0.5F;
                float f4 = -0.0F * (float)(i - 1) * 0.5F;
                float f5 = -0.046875F * (float)(i - 1) * 0.5F;
                GlStateManager.translate(f6, f4, f5);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }

    private int func_177078_a(ItemStack stack)
    {
        int i = 1;

        if (stack.stackSize > 48)
        {
            i = 5;
        }
        else if (stack.stackSize > 32)
        {
            i = 4;
        }
        else if (stack.stackSize > 16)
        {
            i = 3;
        }
        else if (stack.stackSize > 1)
        {
            i = 2;
        }

        return i;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (ItemPhysics.Instance.isEnable()) {
            final EntityItem item;
            final ItemStack itemstack;
            final double rotation = 2;

            if ((itemstack = (item = entity).getEntityItem()).getItem() != null) {
                renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                renderManager.renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                IBakedModel ibakedmodel = itemRenderer.getItemModelMesher().getItemModel(itemstack);

                int i = func_177077_a(item, x, y, z,partialTicks, ibakedmodel,true,0.05f);
                BlockPos pos = new BlockPos(item);

                if (item.rotationPitch > 360.0f) {
                    item.rotationPitch = 0.0f;
                }

                if (!(Double.isNaN(item.getAge()) || Double.isNaN(item.getAir()) || Double.isNaN(item.getEntityId()) || item.getPosition() == null)) {
                    final BlockPos posUp = new BlockPos(item);
                    posUp.add(0, 0.2f, 0);
                    final Material m1 = item.worldObj.getBlockState(posUp).getBlock().getMaterial();
                    final Material m2 = item.worldObj.getBlockState(pos).getBlock().getMaterial();
                    final boolean m3 = item.isInsideOfMaterial(Material.water);
                    final boolean m4 = item.isInWater();

                    if (entity.onGround) {
                        item.rotationPitch = 0f;
                    } else {
                        if (m3 | m1 == Material.water | m2 == Material.water | m4) {
                            item.rotationPitch = (float) ((double) item.rotationPitch + rotation / 4.0);
                        } else {
                            item.rotationPitch = (float) ((double) item.rotationPitch + rotation * 2.0);
                        }
                    }
                }

                GL11.glRotatef(item.rotationYaw, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef((item.rotationPitch + 90.0f), 1.0f, 0.0f, 0.0f);

                for (int j = 0; j < i; j++) {
                    if (ibakedmodel.isAmbientOcclusion()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(0.7f, 0.7f, 0.7f);
                        itemRenderer.renderItem(itemstack, ibakedmodel);
                        GlStateManager.popMatrix();
                    } else {
                        GlStateManager.pushMatrix();

                        if (j > 0) {
                            GlStateManager.translate(0.0f, 0.0f, 0.046875f * j);
                        }

                        itemRenderer.renderItem(itemstack, ibakedmodel);

                        GlStateManager.popMatrix();
                    }
                }

                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }

            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            return;
        }

        ItemStack itemstack = entity.getEntityItem();
        this.field_177079_e.setSeed(187L);
        boolean flag = false;

        if (this.bindEntityTexture(entity))
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            flag = true;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelMesher().getItemModel(itemstack);
        int i = this.func_177077_a(entity, x, y, z, partialTicks, ibakedmodel,false,0.25f);

        for (int j = 0; j < i; ++j)
        {
            if (ibakedmodel.isGui3d())
            {
                GlStateManager.pushMatrix();

                if (j > 0)
                {
                    float f = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f1 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f2 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(f, f1, f2);
                }

                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                this.itemRenderer.renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
            }
            else
            {
                GlStateManager.pushMatrix();
                ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                this.itemRenderer.renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
                float f3 = ibakedmodel.getItemCameraTransforms().ground.scale.x;
                float f4 = ibakedmodel.getItemCameraTransforms().ground.scale.y;
                float f5 = ibakedmodel.getItemCameraTransforms().ground.scale.z;
                GlStateManager.translate(0.0F * f3, 0.0F * f4, 0.046875F * f5);
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);

        if (flag)
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityItem entity)
    {
        return TextureMap.locationBlocksTexture;
    }
}
