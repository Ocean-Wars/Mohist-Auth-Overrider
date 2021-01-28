package fr.oceanwars.authoverrider;

import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class AuthOverrider extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        FileConfiguration config = getConfig();
        final String[] whitelisted_domains = config.getStringList("whitelisted_domains").toArray(new String[0]);
        final String join_url = config.getString("join_url");
        final String check_url = config.getString("check_url");

        setAllFields(whitelisted_domains, join_url, check_url);
    }

    private void setAllFields(String[] whitelisted_domains, String join_url, String check_url) {
        final Class minecraftSession = YggdrasilMinecraftSessionService.class;
        try {
            Field domains = minecraftSession.getDeclaredField("WHITELISTED_DOMAINS");
            Field joinUrl = minecraftSession.getDeclaredField("JOIN_URL");
            Field checkUrl = minecraftSession.getDeclaredField("CHECK_URL");

            setFinalStatic(domains, whitelisted_domains);
            setFinalStatic(joinUrl, HttpAuthenticationService.constantURL(join_url));
            setFinalStatic(checkUrl, HttpAuthenticationService.constantURL(check_url));

            System.out.println("[AuthOverride] Every fields have been override successfully");
            System.out.println("Here are the new URL:");
            System.out.println("WHITELISTED_DOMAINS:");
            Arrays.stream(whitelisted_domains).forEach(domain -> System.out.println("- " + domain));
            System.out.println("JOIN_URL: " + join_url);
            System.out.println("CHECK_URL: " + check_url);
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
