package ihl.explosion;

import java.util.Random;

import ihl.utils.IHLMathUtils;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class ExplosionEntityFX extends EntityFX {
	
	private float radius;
	private static final Random random = new Random();
	private final int[] particlesMaxAge;
	private final int[] particlesTextureIndexX;
	private final int[] particlesTextureIndexY;
	private final double[][] startPos;
	private final double[][] pos;
	private final double[][] prevPos;
	private final float[][] motion;
	private final int[] center;
	
	public ExplosionEntityFX(World world, double x, double y, double z) 
    {
        super(world, x, y, z);
        this.renderDistanceWeight = 5.0D;
		int numParticles = 1;
        particlesMaxAge = new int[numParticles];
    	particlesTextureIndexX = new int[numParticles];
    	particlesTextureIndexY= new int[numParticles];
		startPos = new double[numParticles][3];
		pos = new double[numParticles][3];
		prevPos = new double[numParticles][3];
		motion = new float[numParticles][3];
		center = new int[3];
	}
    
    public ExplosionEntityFX(World par1World, int centerX1, int centerY1, int centerZ1, float radius1)
    {
    	super(par1World, centerX1, centerY1, centerZ1, 0.0D, 0.0D, 0.0D);
    	this.particleMaxAge = 100;
		this.center = new int[3];
    	this.lastTickPosX=this.posX=this.prevPosX=this.center[0]=centerX1;
    	this.lastTickPosY=this.posY=this.prevPosY=this.center[1]=centerY1;
    	this.lastTickPosZ=this.posZ=this.prevPosZ=this.center[2]=centerZ1;
    	this.radius=radius1;
		float r2 = radius*radius;
		int numParticles = Math.min((int)(r2*radius/32)+64,512);
		this.startPos = new double[numParticles][3];
		this.pos = new double[numParticles][3];
		this.prevPos = new double[numParticles][3];
    	this.particleScale *= 40f;
    	this.particlesMaxAge = new int[numParticles];
    	this.particlesTextureIndexX = new int[numParticles];
    	this.particlesTextureIndexY= new int[numParticles];
		this.motion = new float[numParticles][3];
		for(int i=0;i<numParticles;i++)
		{
			float fPosX = random.nextFloat()*2f-1f;
			float fPosY = (random.nextFloat()*2f-1f)*IHLMathUtils.sqrt(1f-fPosX*fPosX);
			float fPosZ = (random.nextFloat()*2f-1f)*IHLMathUtils.sqrt(1f-fPosX*fPosX-fPosY*fPosY);
			fPosX *= radius1;
			fPosY *= radius1;
			fPosZ *= radius1;
			prevPos[i][0] = pos[i][0] = startPos[i][0] = fPosX + centerX1;
			prevPos[i][1] = pos[i][1] = startPos[i][1] = fPosY + centerY1;
			prevPos[i][2] = pos[i][2] = startPos[i][2] = fPosZ + centerZ1;
			particlesMaxAge[i] = random.nextInt(particleMaxAge-20)+16;
	    	particlesTextureIndexY[i]=random.nextInt(8);
		}
    	this.noClip = true;
    	this.renderDistanceWeight = 6.0D+radius1/2;
    }

	@Override
    public void renderParticle(Tessellator tessellator, float interFrame, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY)
    {
		for(int i=0;i<particlesMaxAge.length;i++)
		{
			float u1 = this.particlesTextureIndexX[i] / 16.0F;
        	float u2 = u1 + 1F / 16.0F;
        	float v1 = this.particlesTextureIndexY[i] / 8.0F;
        	float v2 = v1 + 1F / 8.0F;
        	float scale = 0.1F * this.particleScale;

        	float fPosX = (float)(this.prevPos[i][0] - this.prevPosX + (this.pos[i][0] - this.prevPos[i][0]) * interFrame);
        	float fPosY = (float)(this.prevPos[i][1] - this.prevPosY + (this.pos[i][1] - this.prevPos[i][1]) * interFrame);
        	float fPosZ = (float)(this.prevPos[i][2] - this.prevPosZ + (this.pos[i][2] - this.prevPos[i][2]) * interFrame);
        	tessellator.addVertexWithUV(
        			fPosX - rotationX * scale - rotationYZ * scale, 
        			fPosY - rotationXZ * scale, 
        			fPosZ - rotationZ * scale - rotationXY * scale, 
        			u2, v2);
        	tessellator.addVertexWithUV(
        			fPosX - rotationX * scale + rotationYZ * scale, 
        			fPosY + rotationXZ * scale, 
        			fPosZ - rotationZ * scale + rotationXY * scale, 
        			u2, v1);
        	tessellator.addVertexWithUV(
        			fPosX + rotationX * scale + rotationYZ * scale, 
        			fPosY + rotationXZ * scale, 
        			fPosZ + rotationZ * scale + rotationXY * scale, 
        			u1, v1);
        	tessellator.addVertexWithUV(
        			fPosX + rotationX * scale - rotationYZ * scale, 
        			fPosY - rotationXZ * scale, 
        			fPosZ + rotationZ * scale - rotationXY * scale, 
        			u1, v2);
		}
    }
	
    @Override
	public void onUpdate()
    {
		if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
		for(int i=0;i<particlesMaxAge.length;i++)
		{
	        this.prevPos[i][0] = this.pos[i][0];
	        this.prevPos[i][1] = this.pos[i][1];
	        this.prevPos[i][2] = this.pos[i][2];
	        
	        int maxFrameX = 15;
	        int frameX = this.particleAge * maxFrameX / this.particlesMaxAge[i];
	        if(frameX<=maxFrameX)
	        {
	        	this.particlesTextureIndexX[i] = frameX;
	        }
	        
	        if(this.startPos[i][1] < this.center[1])
	        {
	        	IHLMathUtils.vector_add(this.motion[i], 
	        			(float)(this.center[0]-this.pos[i][0])*0.0004f,
	        			0.004f,
	        			(float)(this.center[2]-this.pos[i][2])*0.0004f);
	        }
	        else
	        {
	        	float[] ring = new float[] {
	        			(float)(this.startPos[i][0]-this.center[0]),
	        			0f,
	        			(float)(this.startPos[i][2]-this.center[2])};
	        	IHLMathUtils.scale_vector_to_value(ring, radius*0.6f);
	        	IHLMathUtils.vector_add(ring,
	        			this.center[0],
	        			(float)this.startPos[i][1]+this.particleAge*0.001f,
	        			this.center[2]);
	        	float[] from_point_to_ring = IHLMathUtils.vector_return_difference(ring,this.pos[i]);
	        	float[] from_point_to_center = IHLMathUtils.vector_return_difference(this.center,this.pos[i]);
	        	float[] tangent_to_ring = IHLMathUtils.vector_vector_multiply(from_point_to_ring, from_point_to_center);
	        	float[] tangent_to_torus = IHLMathUtils.vector_vector_multiply(from_point_to_ring, tangent_to_ring);
	        	IHLMathUtils.normalize_vector(tangent_to_torus);
	        	IHLMathUtils.vector_add(this.motion[i],
	        		from_point_to_ring[0]*0.01f + tangent_to_torus[0]*0.02f,
	            	from_point_to_ring[1]*0.01f + tangent_to_torus[1]*0.02f + 0.004f,
	            	from_point_to_ring[2]*0.01f + tangent_to_torus[2]*0.02f);
	        }
	        IHLMathUtils.vector_add(this.pos[i],this.motion[i]);
        	IHLMathUtils.multiply_vector_to_value(this.motion[i], 0.98f);
		}
    }
    
    @Override
    public void setParticleTextureIndex(int par1)
    {
            this.particleTextureIndexX = par1;
    }
    
    @Override
    public boolean shouldRenderInPass(int pass)
    {
    	return pass==1;
    }

}
