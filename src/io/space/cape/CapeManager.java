package io.space.cape;

import io.space.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import utils.hodgepodge.io.IOUtils;
import utils.hodgepodge.object.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public final class CapeManager {
    public static CapeManager Instance;

    private final File CAPE_PATH;
    private final File CURRENT_CAPE_DATA_FILE;

    private final ArrayList<CapeObject> capes = new ArrayList<>();

    private CapeObject currentCape;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CapeManager() {
        CAPE_PATH = new File(Wrapper.Instance.getClientDirectory(),"capes/");

        if (!CAPE_PATH.exists()) {
            CAPE_PATH.mkdir();
        }

        CURRENT_CAPE_DATA_FILE = new File(CAPE_PATH,"CurrentCapeData.str");

        {
            final InputStream stream = IOUtils.getResourceAsStream("assets/space/cape.png");
            FileOutputStream fos = null;

            try {
                final byte[] buffer = new byte[512];
                fos = new FileOutputStream(new File(CAPE_PATH,"defaultCape.png"));

                int i;

                while ((i = stream.read(buffer)) != -1) {
                    fos.write(buffer,0,i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    IOUtils.closeQuietly(stream);
                }

                if (fos != null) {
                    IOUtils.closeQuietly(fos);
                }
            }
        }

        loadCape();
    }

    private void loadCape() {
        final File[] files = CAPE_PATH.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getAbsolutePath().equals(CURRENT_CAPE_DATA_FILE.getAbsolutePath())) continue;

                Wrapper.Instance.getLogger().info("Try load cape {}", file.getAbsolutePath());

                try {
                    final ResourceLocation capeResource = new ResourceLocation("SpaceCapes/" + file.getName());
                    final BufferedImage image = ImageIO.read(file);

                    if (image == null) {
                        Wrapper.Instance.getLogger().error(file.getAbsolutePath() + " not a image");
                    } else {
                        Minecraft.getMinecraft().getTextureManager().loadTexture(capeResource, new DynamicTexture(image));

                        capes.add(new CapeObject(file.getName(), capeResource));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    Wrapper.Instance.getLogger().error("Load cape {} failed!",file.getAbsolutePath());
                }
            }
        }

        BufferedReader reader = null;

        try {
            if (CURRENT_CAPE_DATA_FILE.exists()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(CURRENT_CAPE_DATA_FILE)));

                final String str = reader.readLine();

                if (!StringUtils.isNullOrEmpty(str)) {
                    boolean notFound = true;

                    for (CapeObject cape : capes) {
                        if (cape.getName().equals(str)) {
                            currentCape = cape;
                            notFound = false;
                            break;
                        }
                    }

                    if (notFound) {
                        Wrapper.Instance.getLogger().error("Cannot find current cape {}", str);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    public CapeObject getCurrentCape() {
        return currentCape;
    }

    public void setCurrentCape(CapeObject currentCape) {
        this.currentCape = currentCape;

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CURRENT_CAPE_DATA_FILE)));
            writer.write(currentCape.getName());
        } catch (IOException e) {
            e.printStackTrace();
            Wrapper.Instance.getLogger().error("Write current cape data failed!");
        } finally {
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }
    }

    public ArrayList<CapeObject> getCapes() {
        return capes;
    }
}
