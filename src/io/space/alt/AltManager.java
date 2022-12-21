package io.space.alt;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import io.space.Wrapper;
import io.space.alt.altimpl.MicrosoftAlt;
import io.space.alt.altimpl.MojangAlt;
import io.space.alt.altimpl.OfflineAlt;
import io.space.alt.altimpl.OriginalAlt;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import utils.hodgepodge.io.FileUtils;
import utils.hodgepodge.io.IOUtils;
import utils.hodgepodge.object.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class AltManager {
    public static AltManager Instance;

    private final File ALT_FILE = new File(Wrapper.Instance.getClientDirectory(), "alts.str");
    private final ArrayList<Alt> altList = new ArrayList<>();

    private final JsonParser parser = new JsonParser();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void addAlt(Alt alt) {
        altList.add(alt);
    }

    public ArrayList<Alt> getAltList() {
        return altList;
    }

    public void readAlt() throws IOException {
        altList.clear();

        if (ALT_FILE.exists()) {
            try {
                for (JsonElement element : parser.parse(IOUtils.inputStreamToString(new FileInputStream(ALT_FILE), StandardCharsets.UTF_8)).getAsJsonArray()) {
                    final JsonObject jsonObject = element.getAsJsonObject();

                    final AccountEnum type = AccountEnum.parse(jsonObject.get("AltType").getAsString());
                    final String userName = jsonObject.get("UserName").getAsString();

                    if (type != null) {
                        switch (type) {
                            case OFFLINE:
                                addAlt(new OfflineAlt(userName));
                                break;
                            case MOJANG:
                                addAlt(new MojangAlt(jsonObject.get("Account").getAsString(),jsonObject.get("Password").getAsString(),userName));
                                break;
                            case MICROSOFT:
                                addAlt(new MicrosoftAlt(userName,jsonObject.get("RefreshToken").getAsString()));
                                break;
                            case ORIGINAL:
                                addAlt(new OriginalAlt(userName,jsonObject.get("AccessToken").getAsString(),jsonObject.get("UUID").getAsString(),jsonObject.get("Type").getAsString()));
                                break;
                        }
                    }
                }
            } catch (MalformedJsonException | JsonSyntaxException e) {
                Wrapper.Instance.getLogger().info("Handing old alt file");

                for (String s : FileUtils.readFileAsStringList(ALT_FILE, StandardCharsets.UTF_8)) {
                    final String[] split = s.split(":");
                    final AccountEnum accountType = AccountEnum.parse(split[0]);

                    if (accountType == null) {
                        new RuntimeException("Unknown account type : " + split[0]).printStackTrace();
                        continue;
                    }

                    switch (accountType) {
                        case OFFLINE: {
                            final String userName = split[1];
                            altList.add(new OfflineAlt(userName));
                            break;
                        }
                        case MOJANG: {
                            final String account = split[1];
                            final String password = split[2];
                            final String userName = split[3];

                            altList.add(new MojangAlt(account,password,userName));
                            break;
                        }
                        case MICROSOFT: {
                            final String refreshToken = split[2];

                            altList.add(new MicrosoftAlt(split[1],refreshToken));
                            break;
                        }
                        case ORIGINAL:
                            final String type = split[1];
                            final String userName = split[2];
                            final String token = split[3];
                            final String uuid = split[4];

                            altList.add(new OriginalAlt(userName,token,uuid,type));
                            break;
                    }
                }
            }
        }
    }

    public void saveAlt() throws IOException {
        final JsonArray jsonArray = new JsonArray();

        for (Alt alt : altList) {
            final JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("AltType",alt.getAccountType().getWriteName());
            jsonObject.addProperty("UserName",alt.getUserName());

            if (alt instanceof MicrosoftAlt) {
                jsonObject.addProperty("RefreshToken",((MicrosoftAlt) alt).getRefreshToken());
            } else if (alt instanceof MojangAlt) {
                final MojangAlt mojangAlt = (MojangAlt) alt;

                jsonObject.addProperty("Account",mojangAlt.getAccount());
                jsonObject.addProperty("Password",mojangAlt.getPassword());
            } else if (alt instanceof OriginalAlt) {
                final OriginalAlt originalAlt = (OriginalAlt) alt;

                jsonObject.addProperty("Type",originalAlt.getType());
                jsonObject.addProperty("AccessToken",originalAlt.getAccessToken());
                jsonObject.addProperty("UUID",originalAlt.getUUID());
            }

            jsonArray.add(jsonObject);
        }

        writeStringToFile(gson.toJson(jsonArray),ALT_FILE);
    }

    private void writeStringToFile(String str,File file) throws IOException {
        try (final FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(str.getBytes(StandardCharsets.UTF_8));
        }
    }

    @SuppressWarnings("CaughtExceptionImmediatelyRethrown")
    public static LoginStatus loginAlt(String account, String password) throws AuthenticationException {
        if (StringUtils.isNullOrEmpty(password)) {
            Minecraft.getMinecraft().session = new Session(account, "", "", "mojang");
            return LoginStatus.SUCCESS;
        } else {
            YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(account);
            auth.setPassword(password);

            try {
                auth.logIn();
                Minecraft.getMinecraft().session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");

                return LoginStatus.SUCCESS;
            } catch (AuthenticationException e) {
                throw e;
            }
        }
    }

    public enum LoginStatus {
        FAILED,
        SUCCESS,
        EXCEPTION {
            private Exception exception;

            public Exception getException() {
                return exception;
            }

            public void setException(Exception exception) {
                this.exception = exception;
            }
        }
    }
}
