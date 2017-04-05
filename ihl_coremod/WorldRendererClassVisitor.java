package ihl_coremod;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import net.minecraft.client.renderer.RenderBlocks;

public class WorldRendererClassVisitor extends ClassVisitor {

	public WorldRendererClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals("a") && desc.equals("(Lsv;)V")) {
			IHLCoremod.log.info("Sucessfully founded updateRenderer(Lnet/minecraft/entity/EntityLivingBase;)V");
			IHLCoremod.log.info("Trying intercept: "+Type.getInternalName(RenderBlocks.class)+".<init>");
			return new WorldRendererRenderBlocksInitMethodVisitor(this.api, mv);
		}
		return mv;
	}
}
