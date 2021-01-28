package fr.oceanwars.authoverrider;

import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class AuthOverrider extends JavaPlugin {

    private static final String[] WHITELISTED_DOMAINS = {
            ".minecraft.net",
            ".mojang.com",
            ".localhost",
            ".ocanwars.fr"
    };
    private static final String JOIN_URL = "http://authserver.localhost/join";
    private static final String CHECK_URL = "http://authserver.localhost/hasJoined";

    @Override
    public void onEnable() {
        setAllFields();
    }

    private void setAllFields() {
        final Class minecraftSession = YggdrasilMinecraftSessionService.class;
        try {
            Field domains = minecraftSession.getDeclaredField("WHITELISTED_DOMAINS");
            Field joinUrl = minecraftSession.getDeclaredField("JOIN_URL");
            Field checkUrl = minecraftSession.getDeclaredField("CHECK_URL");

            setFinalStatic(domains, WHITELISTED_DOMAINS);
            setFinalStatic(joinUrl, HttpAuthenticationService.constantURL(JOIN_URL));
            setFinalStatic(checkUrl, HttpAuthenticationService.constantURL(CHECK_URL));

            System.out.println("[AuthOverride] Every fields have been override successfully");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }

    static void setFinalStatic(Field field, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
