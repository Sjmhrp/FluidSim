constant sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST;
constant sampler_t samplerNormal = CLK_NORMALIZED_COORDS_TRUE | CLK_ADDRESS_REPEAT | CLK_FILTER_NEAREST;

float4 sample(read_only image2d_t image, float2 pos) {
	//if(pos.x<0||pos.x>1||pos.y<0||pos.y>1)return (float4)(0,0,0,0);
	return read_imagef(image,samplerNormal,pos);
}

float4 bilerp(float2 pos, read_only image2d_t image, const float size) {
	float x = pos.x/size-0.5;
	float y = pos.y/size-0.5;
	float fractX = fract(x,&x);
	float fractY = fract(y,&y);
	x+=0.5;
	y+=0.5;
	x*=size;
	y*=size;
	return mix(mix(sample(image,(float2)(x,y)),sample(image,(float2)(x+size,y)),fractX),mix(sample(image,(float2)(x,y+size)),sample(image,(float2)(x+size,y+size)),fractX),fractY);
}

kernel void initialize(write_only image2d_t velocityOut, write_only image2d_t colourOut, const int gridSize) {
	const int x = get_global_id(0);
	const int y = get_global_id(1);
	float m = gridSize/8.-0.5;
	const int square = (int)(step((float)fmod(x,gridSize/4.),m))^(int)(step((float)fmod(y,gridSize/4.),m));
	float r = step(1.0,fmod(floor((x/(float)gridSize*2)/0.2)+floor((y/(float)gridSize*2)/0.2),2.0));
	float g = step(1.0,fmod(floor((x/(float)gridSize*2)/0.3)+floor((y/(float)gridSize*2)/0.3),2.0));
	float b = step(1.0,fmod(floor((x/(float)gridSize*2)/0.4)+floor((y/(float)gridSize*2)/0.4),2.0));
	//float r = step(y/(float)gridSize*6);
	//write_imagef(colourOut,(int2)(x,y),(float4)(square,square,square,1));
	write_imagef(colourOut,(int2)(x,y),(float4)(r,g,b,1));
	
	//write_imagef(velocityOut,(int2)(x,y),(float4)(0,0,0,0));
	write_imagef(velocityOut,(int2)(x,y),(float4)(sin(6.28*(y+0.5)/gridSize),sin(6.28*(x+0.5)/gridSize),0,0)/20);
}

kernel void advect(read_only image2d_t velocityIn, write_only image2d_t velocityOut, read_only image2d_t colourIn, write_only image2d_t colourOut, const float size, const float dt, const float forceX, const float forceY, const float mouseX, const float mouseY) {
	const int x = get_global_id(0);
	const int y = get_global_id(1);
	float2 pos = (float2)(x+0.5,y+0.5)*size;
	pos-=read_imagef(velocityIn,sampler,(int2)(x,y)).xy*dt;
	write_imagef(colourOut,(int2)(x,y),bilerp(pos,colourIn,size));
	float2 force = (float2)(forceX,forceY)*exp(-distance(pos,(float2)(mouseX,mouseY))*10);
	write_imagef(velocityOut,(int2)(x,y),bilerp(pos,velocityIn,size)+(float4)(force,0,0));
}

kernel void divergence(read_only image2d_t velocityIn, write_only image2d_t pressureOut, const float size) {
	const float x = (get_global_id(0)+0.5)*size;
	const float y = (get_global_id(1)+0.5)*size;
	float d = bilerp((float2)(x+size,y),velocityIn,size).x
	-bilerp((float2)(x-size,y),velocityIn,size).x
	+bilerp((float2)(x,y+size),velocityIn,size).y
	-bilerp((float2)(x,y-size),velocityIn,size).y;
	d*=-0.5;
	write_imagef(pressureOut,(int2)(get_global_id(0),get_global_id(1)),(float4)(0,0,0,d));
}

kernel void jacobiPressure(read_only image2d_t pressureIn, write_only image2d_t pressureOut, const float size) {
	const float x = (get_global_id(0)+0.5)*size;
	const float y = (get_global_id(1)+0.5)*size;
	float d = bilerp((float2)(x,y),pressureIn,size).w;
	float4 p1 = bilerp((float2)(x+size,y),pressureIn,size);
	float4 p2 = bilerp((float2)(x-size,y),pressureIn,size);
	float4 p3 = bilerp((float2)(x,y+size),pressureIn,size);
	float4 p4 = bilerp((float2)(x,y-size),pressureIn,size);
	write_imagef(pressureOut,(int2)(get_global_id(0),get_global_id(1)),(float4)((p1.x+p2.x+p3.x+p4.x+d)/4,0,0,d));
}

kernel void subtractPressure(read_only image2d_t velocityIn, read_only image2d_t pressureIn, write_only image2d_t velocityOut, const float size) {
	int2 pos = (int2)(get_global_id(0),get_global_id(1));
	float x = (pos.x+0.5)*size;
	float y = (pos.y+0.5)*size;
	float2 grad = (float2)(
	bilerp((float2)(x+size,y),pressureIn,size).x
	-bilerp((float2)(x-size,y),pressureIn,size).x
	,bilerp((float2)(x,y+size),pressureIn,size).x
	-bilerp((float2)(x,y-size),pressureIn,size).x
	);
	grad/=2;
	//write_imagef(velocityOut,pos,read_imagef(velocityIn,sampler,pos));
	write_imagef(velocityOut,pos,read_imagef(velocityIn,sampler,pos)-(float4)(grad,0,0));
	//if(get_global_id(0)==0||get_global_id(255)||get_global_id(1)==0||get_global_id(1)==255)write_imagef(velocityOut,pos,(float4)(0,0,0,0));
}