package dev.darkblade.mod.input_engine.client;

import net.neoforged.neoforge.network.PacketDistributor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CheckPD {
    public static void main(String[] args) {
        for (Method m : PacketDistributor.class.getDeclaredMethods()) {
            if (Modifier.isStatic(m.getModifiers())) {
                System.out.println(m.toString());
            }
        }
    }
}
