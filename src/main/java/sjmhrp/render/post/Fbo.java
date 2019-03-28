package sjmhrp.render.post;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static sjmhrp.render.post.Fbo.FboType.DEPTH_STENCIL_BUFFER;
import static sjmhrp.render.post.Fbo.FboType.DEPTH_TEXTURE;
import static sjmhrp.render.post.Fbo.FboType.TEXTURE_3D;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

public class Fbo {

	public static final int COLOUR_ATTACHMENTS = 3;
	
    private final int width;
    private final int height;
    private int depth;
 
    private boolean multi = false;
    
    private int frameBuffer;
 
    private int colourTexture;
    private int depthTexture;
 
    private int depthBuffer;
    private int[] colourBuffers = new int[COLOUR_ATTACHMENTS];
    
    private int depthStencilBuffer;
    
    public Fbo(int width, int height, FboType depthBufferType) {
        this.width = width;
        this.height = height;
        initialiseFrameBuffer(depthBufferType);
    }
 
    public Fbo(int width, int height) {
        this.width = width;
        this.height = height;
        this.multi = true;
        initialiseFrameBuffer(DEPTH_STENCIL_BUFFER);
    }
    
    public Fbo(int width, int height, int depth) {
    	this.width=width;
    	this.height=height;
    	this.depth=depth;
    	initialiseFrameBuffer(TEXTURE_3D);
    }
    
    
    
    public void cleanUp() {
        glDeleteFramebuffers(frameBuffer);
        glDeleteTextures(colourTexture);
        glDeleteTextures(depthTexture);
        glDeleteRenderbuffers(depthBuffer);
        for(int i = 0; i < colourBuffers.length; i++) {
        	glDeleteRenderbuffers(colourBuffers[i]);
        }
    }
 
    public void bindFrameBuffer() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
        glViewport(0, 0, width, height);
    }

    public void unbindFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
    }
 
    public int getColourTexture() {
        return colourTexture;
    }
 
    public int getDepthTexture() {
        return depthTexture;
    }
    
    public int getDepthBuffer() {
		return depthBuffer;
	}

	public int getDepthStencilBuffer() {
		return depthStencilBuffer;
	}

	public int getFrameBuffer() {
    	return frameBuffer;
    }
    
    public int getWidth() {
    	return width;
    }
    
    public int getHeight() {
    	return height;
    }
    
    public void resolve(int readBuffer, Fbo output) {
    	glBindFramebuffer(GL_DRAW_FRAMEBUFFER, output.frameBuffer);
    	glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer);
    	glReadBuffer(GL_COLOR_ATTACHMENT0+readBuffer);
    	glBlitFramebuffer(0, 0, width, height, 0, 0, output.width,output.height,GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT,GL_NEAREST);
    	unbindFrameBuffer();
    }

    public void resolveDepth(Fbo output) {
    	glBindFramebuffer(GL_DRAW_FRAMEBUFFER,output.frameBuffer);
    	glBindFramebuffer(GL_READ_FRAMEBUFFER,this.frameBuffer);
    	glReadBuffer(GL_DEPTH_STENCIL_ATTACHMENT);
    	glBlitFramebuffer(0, 0, width, height, 0, 0, output.width, output.height,GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT, GL_NEAREST);
    	unbindFrameBuffer();
    }
    
    private void initialiseFrameBuffer(FboType type) {
        createFrameBuffer();
        if(multi) {
        	for(int i = 0; i < colourBuffers.length; i++) {
        		createMultiColourAttachment(i);
        	}
        } else if(type==TEXTURE_3D) {
        	create3DTextureAttachment();
        } else {
        	createTextureAttachment();
        }
        if (type == DEPTH_TEXTURE) {
        	createStencilDepthTexture();
        } else if (type == DEPTH_STENCIL_BUFFER) {
        	createDepthStencilBuffer();
        }
        unbindFrameBuffer();
    }
 
    private void createFrameBuffer() {
        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        determineDrawBuffers();
    }
 
    private void determineDrawBuffers() {
    	IntBuffer drawBuffers = BufferUtils.createIntBuffer(COLOUR_ATTACHMENTS);
    	drawBuffers.put(GL_COLOR_ATTACHMENT0);
    	if(multi) {
    		for(int i = 1; i < colourBuffers.length; i++) {
    			drawBuffers.put(GL_COLOR_ATTACHMENT0+i);
    		}
    	}
    	drawBuffers.flip();
    	glDrawBuffers(drawBuffers);
    }
    
    private void createTextureAttachment() {
        colourTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, colourTexture);
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA32F,width,height,0,GL_RGBA,GL_FLOAT,(ByteBuffer)null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colourTexture, 0);
    }
    
    private void create3DTextureAttachment() {
    	colourTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_3D,colourTexture);
        glTexImage3D(GL_TEXTURE_3D,0,GL_RGBA32F,width,height,depth,0,GL_RGBA,GL_FLOAT,(ByteBuffer)null);
        glTexParameteri(GL_TEXTURE_3D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_3D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_3D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_3D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_3D,GL_TEXTURE_WRAP_R,GL_CLAMP_TO_EDGE);
        glFramebufferTexture(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,colourTexture,0);
    }
    
    private void createStencilDepthTexture() {
    	depthTexture = glGenTextures();
    	glBindTexture(GL_TEXTURE_2D, depthTexture);
    	glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH32F_STENCIL8, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    	glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
    }
   
    private void createMultiColourAttachment(int i) {
    	colourBuffers[i] = glGenRenderbuffers();
    	glBindRenderbuffer(GL_RENDERBUFFER, colourBuffers[i]);
    	glRenderbufferStorage(GL_RENDERBUFFER,GL_RGBA32F, width, height);
    	glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, GL_RENDERBUFFER, colourBuffers[i]);
    }
    
    private void createDepthStencilBuffer() {
    	depthStencilBuffer = glGenRenderbuffers();
    	glBindRenderbuffer(GL_RENDERBUFFER, depthStencilBuffer);
    	glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH32F_STENCIL8, width, height);
    	glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthStencilBuffer);
    }
    
    public static enum FboType {
    	DEPTH_TEXTURE,
    	DEPTH_STENCIL_BUFFER,
    	TEXTURE_3D
    }
}