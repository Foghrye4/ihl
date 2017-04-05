package ihl_coremod;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WorldRendererRenderBlocksInitMethodVisitor extends MethodVisitor {

	private static final String RENDER_BLOCK_EXT_TYPE = "ihl/model/RenderBlocksExt";
	private static final String RENDER_BLOCK_TYPE = "blm";

	public WorldRendererRenderBlocksInitMethodVisitor(int api, MethodVisitor mv) {
		super(api, mv);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		if (opcode == Opcodes.NEW && type.equals(RENDER_BLOCK_TYPE)) {
			IHLCoremod.log.info("Sucessfully intercept new RenderBlocks instruction.");
			super.visitTypeInsn(opcode, RENDER_BLOCK_EXT_TYPE);
		} else {
			super.visitTypeInsn(opcode, type);
		}
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		if (owner.equals(RENDER_BLOCK_TYPE) && name.equals("<init>")) {
			IHLCoremod.log.info("Sucessfully intercept RenderBlocks.<init> method.");
			super.visitMethodInsn(opcode, RENDER_BLOCK_EXT_TYPE, name, desc, itf);
		} else {
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}
	}
}
