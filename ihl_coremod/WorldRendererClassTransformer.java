package ihl_coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class WorldRendererClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (name.equals("blo")) {
			IHLCoremod.log.info("net.minecraft.client.renderer.WorldRenderer founded.");
			ClassReader cr = new ClassReader(basicClass);
			ClassWriter cw = new ClassWriter(cr, 0);
			WorldRendererClassVisitor cv = new WorldRendererClassVisitor(Opcodes.ASM4, cw);
			cr.accept(cv, 0);
			return cw.toByteArray();
		}
		return basicClass;
	}
}
