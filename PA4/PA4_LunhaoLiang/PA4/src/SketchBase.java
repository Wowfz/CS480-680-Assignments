//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)
//   Modify by Lunhao Liang on Dec 11


import java.awt.image.BufferedImage;

public class SketchBase 
{
	public SketchBase()
	{
		// deliberately left blank
	}
	
	/**********************************************************************
	 * Draws a point.
	 * This is achieved by changing the color of the buffer at the location
	 * corresponding to the point. 
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p
	 *          Point to be drawn.
	 */
	public static void drawPoint(BufferedImage buff, Point2D_depth p)
	{
		if(p.x>=0 && p.x<buff.getWidth() && p.y>=0 && p.y < buff.getHeight())
			buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getRGB_int());	
	}

	public static void drawDepthPoint(BufferedImage buff, Point2D_depth p, float[][] depthbuffer)
	{
		if(p.x>=0 && p.x< buff.getWidth() && p.y>=0 && p.y < buff.getHeight()){
			if(p.z > depthbuffer[p.x][p.y]){
				buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getRGB_int());
				depthbuffer[p.x][p.y] = p.z;
			}
		}
	}

	/**********************************************************************
	 * Draws a line segment using Bresenham's algorithm, linearly 
	 * interpolating RGB color along line segment.
	 * This method only uses integer arithmetic.
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given endpoint of the line.
	 * @param p2
	 *          Second given endpoint of the line.
	 */
	public static void drawLine(BufferedImage buff, Point2D_depth p1, Point2D_depth p2, float[][] depthbuffer)
	{
	    int x0=p1.x, y0=p1.y;
	    int xEnd=p2.x, yEnd=p2.y;
	    int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0);

	    if(dx==0 && dy==0)
	    {
	    	drawDepthPoint(buff,p1,depthbuffer);
	    	return;
	    }
	    
	    // if slope is greater than 1, then swap the role of x and y
	    boolean x_y_role_swapped = (dy > dx); 
	    if(x_y_role_swapped)
	    {
	    	x0=p1.y; 
	    	y0=p1.x;
	    	xEnd=p2.y; 
	    	yEnd=p2.x;
	    	dx = Math.abs(xEnd - x0);
	    	dy = Math.abs(yEnd - y0);
	    }
	    
	    // initialize the decision parameter and increments
	    int p = 2 * dy - dx;
	    int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
	    int x=x0, y=y0;
	    int z =p1.z;
	    
	    // set step increment to be positive or negative
	    int step_x = x0<xEnd ? 1 : -1;
	    int step_y = y0<yEnd ? 1 : -1;
	    
	    // deal with setup for color interpolation
	    // first get r,g,b integer values at the end points
	    int r0=p1.c.getR_int(), rEnd=p2.c.getR_int();
	    int g0=p1.c.getG_int(), gEnd=p2.c.getG_int();
	    int b0=p1.c.getB_int(), bEnd=p2.c.getB_int();
	    
	    // compute the change in r,g,b 
	    int dr=Math.abs(rEnd-r0), dg=Math.abs(gEnd-g0), db=Math.abs(bEnd-b0);
	    
	    // set step increment to be positive or negative 
	    int step_r = r0<rEnd ? 1 : -1;
	    int step_g = g0<gEnd ? 1 : -1;
	    int step_b = b0<bEnd ? 1 : -1;
	    
	    // compute whole step in each color that is taken each time through loop
	    int whole_step_r = step_r*(dr/dx);
	    int whole_step_g = step_g*(dg/dx);
	    int whole_step_b = step_b*(db/dx);
	    
	    // compute remainder, which will be corrected depending on decision parameter
	    dr=dr%dx;
	    dg=dg%dx; 
	    db=db%dx;
	    
	    // initialize decision parameters for red, green, and blue
	    int p_r = 2 * dr - dx;
	    int twoDr = 2 * dr,  twoDrMinusDx = 2 * (dr - dx);
	    int r=r0;
	    
	    int p_g = 2 * dg - dx;
	    int twoDg = 2 * dg,  twoDgMinusDx = 2 * (dg - dx);
	    int g=g0;
	    
	    int p_b = 2 * db - dx;
	    int twoDb = 2 * db,  twoDbMinusDx = 2 * (db - dx);
	    int b=b0;
	    
	    // draw start pixel
	    if(x_y_role_swapped)
	    {
	    	if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth())
	    		if(depthbuffer[y][x] < z)
	    		buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);
	    		depthbuffer[y][x] = z;
	    }
	    else
	    {
	    	if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth())
				if(depthbuffer[x][y] < z)
	    		buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);
				depthbuffer[x][y] = z;
	    }
	    
	    while (x != xEnd) 
	    {
	    	// increment x and y
	    	x+=step_x;
	    	if (p < 0)
	    		p += twoDy;
	    	else 
	    	{
	    		y+=step_y;
	    		p += twoDyMinusDx;
	    	}
		        
	    	// increment r by whole amount slope_r, and correct for accumulated error if needed
	    	r+=whole_step_r;
	    	if (p_r < 0)
	    		p_r += twoDr;
	    	else 
	    	{
	    		r+=step_r;
	    		p_r += twoDrMinusDx;
	    	}
		    
	    	// increment g by whole amount slope_b, and correct for accumulated error if needed  
	    	g+=whole_step_g;
	    	if (p_g < 0)
	    		p_g += twoDg;
	    	else 
	    	{
	    		g+=step_g;
	    		p_g += twoDgMinusDx;
	    	}
		    
	    	// increment b by whole amount slope_b, and correct for accumulated error if needed
	    	b+=whole_step_b;
	    	if (p_b < 0)
	    		p_b += twoDb;
	    	else 
	    	{
	    		b+=step_b;
	    		p_b += twoDbMinusDx;
	    	}
		    
	    	if(x_y_role_swapped)
	    	{
	    		if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth())
	    			buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);
	    	}
	    	else
	    	{
	    		if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth())
	    			buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);
	    	}
	    }
	}

	/**********************************************************************
	 * Draws a filled triangle. 
	 * The triangle may be filled using flat fill or smooth fill. 
	 * This routine fills columns of pixels within the left-hand part, 
	 * and then the right-hand part of the triangle.
	 *   
	 *	                         *
	 *	                        /|\
	 *	                       / | \
	 *	                      /  |  \
	 *	                     *---|---*
	 *	            left-hand       right-hand
	 *	              part             part
	 *
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @param do_smooth
	 *          Flag indicating whether flat fill or smooth fill should be used.                   
	 */



	// draw a triangle
	public static void drawTriangle(BufferedImage buff, Point2D_depth p1, Point2D_depth p2, Point2D_depth p3, boolean do_smooth, float[][] depthbuffer)
	{
		Point2D_depth v1 = p1, v2 = p2, v3 = p3;

		//sort v1, v2, v3 by their y value, v1 > v2 > v3
		if(v1.y<=v2.y)
		{
			Point2D_depth temp = v1;
			v1 = v2;
			v2 = temp;
		}
		if(v2.y<=v3.y)
		{
			Point2D_depth temp = v2;
			v2 = v3;
			v3 = temp;
		}
		if(v2.y >= v1.y)
		{
			Point2D_depth temp = v1;
			v1 = v2;
			v2 = temp;
		}

		// define color and color of points a and b, and color at coordinant
		ColorType color1 = v1.c;
		ColorType color2 = v2.c;
		ColorType color3 = v3.c;
		ColorType colora = new ColorType(0.0f,0.0f,0.0f);
		ColorType colorb = new ColorType(0.0f,0.0f,0.0f);
		ColorType color = new ColorType(0.0f,0.0f,0.0f);

		//the inverse of slope between v2, v1 and v3, v1
		float invslope1 = (float)(v2.x - v1.x) / (float)(v2.y - v1.y);
		float invslope2 = (float)(v3.x - v1.x) / (float)(v3.y - v1.y);
		float invslope_z1 =  (float)(v2.z - v1.z)/(float)(v2.y - v1.y);
		float invslope_z2 =  (float)(v3.z - v1.z)/(float)(v3.y - v1.y);

		//the current x value on line v1v2 and v1v3(point pa and pb)
		float curx1 = v1.x;
		float curx2 = v1.x;
		float curz1 = v1.z;
		float curz2 = v1.z;

		// current y value
		int scanlineY = v1.y;

		// draw upper triangle
		for (scanlineY = v1.y; scanlineY >= v2.y; scanlineY--)
		{
			// calculate pa's color and pb's color
			colora.r = color2.r*((float)(v1.y - scanlineY)/(float)(v1.y - v2.y)) + color1.r*((float)(scanlineY - v2.y)/(float)(v1.y - v2.y));
			colora.g = color2.g*((float)(v1.y - scanlineY)/(float)(v1.y - v2.y)) + color1.g*((float)(scanlineY - v2.y)/(float)(v1.y - v2.y));
			colora.b = color2.b*((float)(v1.y - scanlineY)/(float)(v1.y - v2.y)) + color1.b*((float)(scanlineY - v2.y)/(float)(v1.y - v2.y));

			colorb.r = color3.r*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + color1.r*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			colorb.g = color3.g*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + color1.g*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			colorb.b = color3.b*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + color1.b*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));

			// define pa pb
			Point2D_depth pa = new Point2D_depth((int)curx1,scanlineY,(int)curz1,colora);
			Point2D_depth pb = new Point2D_depth((int)curx2,scanlineY,(int)curz2,colorb);

			float interploation_z = pa.z;
			float invslope_z =(float)(pb.z - pa.z)/(float)(pb.x - pa.x);

			// if pa.x<pb.x
			if(pa.x<pb.x)
				for(int i = pa.x; i <= pb.x;i++)
				{

					if(do_smooth)
					{
						// calculate the color of point by using bilinear interpolation method
						color.r = colora.r*((float)(pb.x - i)/(float)(pb.x - pa.x)) + colorb.r*((float)(i - pa.x)/(float)(pb.x - pa.x));
						color.g = colora.g*((float)(pb.x - i)/(float)(pb.x - pa.x)) + colorb.g*((float)(i - pa.x)/(float)(pb.x - pa.x));
						color.b = colora.b*((float)(pb.x - i)/(float)(pb.x - pa.x)) + colorb.b*((float)(i - pa.x)/(float)(pb.x - pa.x));
					}
					else
						color = p1.c;

					//draw point
					Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,color);

					drawDepthPoint(buff,pi,depthbuffer);
					interploation_z+=invslope_z;
				}

			// if pa.x>pb.x
			if(pa.x>pb.x)
				for(int i = pa.x; i >= pb.x;i--)
				{
					if(do_smooth)
					{
						// calculate the color of point by using bilinear interpolation method
						color.r = colora.r*((float)(i - pb.x)/(float)(pa.x - pb.x)) + colorb.r*((float)(pa.x - i)/(float)(pa.x - pb.x));
						color.g = colora.g*((float)(i - pb.x)/(float)(pa.x - pb.x)) + colorb.g*((float)(pa.x - i)/(float)(pa.x - pb.x));
						color.b = colora.b*((float)(i - pb.x)/(float)(pa.x - pb.x)) + colorb.b*((float)(pa.x - i)/(float)(pa.x - pb.x));
					}
					else
						color = p1.c;

					//draw point
					Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,color);

					drawDepthPoint(buff,pi,depthbuffer);
					interploation_z-=invslope_z;
				}

			// calculate the pa.x and pb.x on next line
			curx1 -= invslope1;
			curx2 -= invslope2;
			curz1 -= invslope_z1;
			curz2 -= invslope_z2;
		}


		// define the similar value like above for drawing lower triangle
		curx1 = v3.x;
		curx2 = v3.x;
		curz1 = v3.z;
		curz2 = v3.z;

		invslope1 = (float)(v2.x - v3.x) / (float)(v2.y - v3.y);
		invslope2 = (float)(v1.x - v3.x) / (float)(v1.y - v3.y);
		invslope_z1 =  (float)(v2.z - v3.z)/(float)(v2.y - v3.y);
		invslope_z2 =  (float)(v1.z - v3.z)/(float)(v1.y - v3.y);

		// drawing lower triangle
		for (scanlineY = v3.y; scanlineY <= v2.y; scanlineY++)
		{
			// calculate pa's color and pb's color
			colora.r = color3.r*((float)(v2.y - scanlineY)/(float)(v2.y - v3.y)) + color2.r*((float)(scanlineY - v3.y)/(float)(v2.y - v3.y));
			colora.g = color3.g*((float)(v2.y - scanlineY)/(float)(v2.y - v3.y)) + color2.g*((float)(scanlineY - v3.y)/(float)(v2.y - v3.y));
			colora.b = color3.b*((float)(v2.y - scanlineY)/(float)(v2.y - v3.y)) + color2.b*((float)(scanlineY - v3.y)/(float)(v2.y - v3.y));

			colorb.r = color3.r*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + color1.r*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			colorb.g = color3.g*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + color1.g*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			colorb.b = color3.b*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + color1.b*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));

			Point2D_depth pa = new Point2D_depth((int)curx1,scanlineY,(int)curz1,colora);
			Point2D_depth pb = new Point2D_depth((int)curx2,scanlineY,(int)curz2,colorb);

			float interploation_z = pa.z;
			float invslope_z =(float)(pb.z - pa.z)/(float)(pb.x - pa.x);

			if(pa.x<pb.x)
				for(int i = pa.x; i <= pb.x;i++)
				{
					if(do_smooth)
					{
						// calculate the color of point by using bilinear interpolation method
						color.r = colora.r*((float)(pb.x - i)/(float)(pb.x - pa.x)) + colorb.r*((float)(i - pa.x)/(float)(pb.x - pa.x));
						color.g = colora.g*((float)(pb.x - i)/(float)(pb.x - pa.x)) + colorb.g*((float)(i - pa.x)/(float)(pb.x - pa.x));
						color.b = colora.b*((float)(pb.x - i)/(float)(pb.x - pa.x)) + colorb.b*((float)(i - pa.x)/(float)(pb.x - pa.x));
					}
					else
						color = p1.c;

					//draw point
					Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,color);

					drawDepthPoint(buff,pi,depthbuffer);
					interploation_z+=invslope_z;
				}
			if(pa.x>pb.x)
				for(int i = pa.x; i >= pb.x;i--)
				{
					if(do_smooth)
					{
						// calculate the color of point by using bilinear interpolation method
						color.r = colora.r*((float)(i - pb.x)/(float)(pa.x - pb.x)) + colorb.r*((float)(pa.x - i)/(float)(pa.x - pb.x));
						color.g = colora.g*((float)(i - pb.x)/(float)(pa.x - pb.x)) + colorb.g*((float)(pa.x - i)/(float)(pa.x - pb.x));
						color.b = colora.b*((float)(i - pb.x)/(float)(pa.x - pb.x)) + colorb.b*((float)(pa.x - i)/(float)(pa.x - pb.x));
					}
					else
						color = p1.c;

					//draw point
					Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,color);

					drawDepthPoint(buff,pi,depthbuffer);
					interploation_z-=invslope_z;
				}

			// calculate the pa.x and pb.x on next line
			curx1 += invslope1;
			curx2 += invslope2;
			curz1 += invslope_z1;
			curz2 += invslope_z2;
		}
	}

	public static void drawTriangle_Phong_Shading(BufferedImage buff, Point2D_depth tri1, Point2D_depth tri2, Point2D_depth tri3, Vector3D tri1_n, Vector3D tri2_n, Vector3D tri3_n, Material mat, Vector3D view_vector, boolean do_smooth, float[][] depthbuffer, InfiniteLight infinitelight, PointLight pointlight, AmbientLight ambienlight, boolean light, boolean infinte, boolean point, boolean amb)
	{
		tri1.n = tri1_n;
		tri2.n = tri2_n;
		tri3.n = tri3_n;

		Point2D_depth v1 = tri1, v2 = tri2, v3 = tri3;

		//sort v1, v2, v3 by their y value, v1 > v2 > v3
		if(v1.y<=v2.y)
		{
			Point2D_depth temp = v1;
			v1 = v2;
			v2 = temp;
		}
		if(v2.y<=v3.y)
		{
			Point2D_depth temp = v2;
			v2 = v3;
			v3 = temp;
		}
		if(v2.y >= v1.y)
		{
			Point2D_depth temp = v1;
			v1 = v2;
			v2 = temp;
		}



		Vector3D normal1 = v1.n;
		Vector3D normal2 = v2.n;
		Vector3D normal3 = v3.n;


		Vector3D normala = new Vector3D(0,0,0);
		Vector3D normalb = new Vector3D(0,0,0);
		Vector3D normal = new Vector3D(0,0,0);


		//the inverse of slope between v2, v1 and v3, v1
		float invslope1 = (float)(v2.x - v1.x) / (float)(v2.y - v1.y);
		float invslope2 = (float)(v3.x - v1.x) / (float)(v3.y - v1.y);
		float invslope_z1 =  (float)(v2.z - v1.z)/(float)(v2.y - v1.y);
		float invslope_z2 =  (float)(v3.z - v1.z)/(float)(v3.y - v1.y);

		//the current x value on line v1v2 and v1v3(point pa and pb)
		float curx1 = v1.x;
		float curx2 = v1.x;
		float curz1 = v1.z;
		float curz2 = v1.z;

		// current y value
		int scanlineY = v1.y;

		// draw upper triangle
		for (scanlineY = v1.y; scanlineY >= v2.y; scanlineY--)
		{
			// calculate pa's color and pb's color
			normala.x = normal2.x*((float)(v1.y - scanlineY)/(float)(v1.y - v2.y)) + normal1.x*((float)(scanlineY - v2.y)/(float)(v1.y - v2.y));
			normala.y = normal2.y*((float)(v1.y - scanlineY)/(float)(v1.y - v2.y)) + normal1.y*((float)(scanlineY - v2.y)/(float)(v1.y - v2.y));
			normala.z = normal2.z*((float)(v1.y - scanlineY)/(float)(v1.y - v2.y)) + normal1.z*((float)(scanlineY - v2.y)/(float)(v1.y - v2.y));



			normalb.x = normal3.x*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + normal1.x*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			normalb.y = normal3.y*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + normal1.y*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			normalb.z = normal3.z*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + normal1.z*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));


			// define pa pb
			Point2D_depth pa = new Point2D_depth((int)curx1,scanlineY,(int)curz1,normala);
			Point2D_depth pb = new Point2D_depth((int)curx2,scanlineY,(int)curz2,normalb);


			float interploation_z = pa.z;
			float invslope_z =(float)(pb.z - pa.z)/(float)(pb.x - pa.x);

			// if pa.x<pb.x
			if(pa.x<pb.x)
				for(int i = pa.x; i <= pb.x;i++)
				{

						// calculate the color of point by using bilinear interpolation method

						normal.x = normala.x*((float)(pb.x - i)/(float)(pb.x - pa.x)) + normalb.x*((float)(i - pa.x)/(float)(pb.x - pa.x));
						normal.y = normala.y*((float)(pb.x - i)/(float)(pb.x - pa.x)) + normalb.y*((float)(i - pa.x)/(float)(pb.x - pa.x));
						normal.z = normala.z*((float)(pb.x - i)/(float)(pb.x - pa.x)) + normalb.z*((float)(i - pa.x)/(float)(pb.x - pa.x));


					//draw point
					Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,normal);
					//Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,color);

					pi.c.setR_int(0);pi.c.setG_int(0);pi.c.setB_int(0);
					if(amb && light) {
						pi.c.r += ambienlight.applyLight(mat).r;
						pi.c.g += ambienlight.applyLight(mat).g;
						pi.c.b += ambienlight.applyLight(mat).b;
					}

					if(point && light) {
						Vector3D v = new Vector3D(pi.x, pi.y, pi.z);
						pi.c.r += pointlight.applyLight(mat, view_vector, pi.n, v).r;
						pi.c.g += pointlight.applyLight(mat, view_vector, pi.n, v).g;
						pi.c.b += pointlight.applyLight(mat, view_vector, pi.n, v).b;
					}

					if(infinte && light) {
						pi.c.r += infinitelight.applyLight(mat, view_vector, pi.n).r;
						pi.c.g += infinitelight.applyLight(mat, view_vector, pi.n).g;
						pi.c.b += infinitelight.applyLight(mat, view_vector, pi.n).b;
					}
					pi.c.r = (float) Math.min(1.0, pi.c.r);
					pi.c.g = (float) Math.min(1.0, pi.c.g);
					pi.c.b = (float) Math.min(1.0, pi.c.b);

					drawDepthPoint(buff,pi,depthbuffer);
					interploation_z+=invslope_z;
				}

			// if pa.x>pb.x
			if(pa.x>pb.x)
				for(int i = pa.x; i >= pb.x;i--)
				{

						// calculate the color of point by using bilinear interpolation method
						normal.x = normala.x*((float)(i - pb.x)/(float)(pa.x - pb.x)) + normalb.x*((float)(pa.x - i)/(float)(pa.x - pb.x));
						normal.y = normala.y*((float)(i - pb.x)/(float)(pa.x - pb.x)) + normalb.y*((float)(pa.x - i)/(float)(pa.x - pb.x));
						normal.z = normala.z*((float)(i - pb.x)/(float)(pa.x - pb.x)) + normalb.z*((float)(pa.x - i)/(float)(pa.x - pb.x));


					//draw point
					Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,normal);

					pi.c.setR_int(0);pi.c.setG_int(0);pi.c.setB_int(0);
					if(amb && light) {
						pi.c.r += ambienlight.applyLight(mat).r;
						pi.c.g += ambienlight.applyLight(mat).g;
						pi.c.b += ambienlight.applyLight(mat).b;
					}

					if(point && light) {
						Vector3D v = new Vector3D(pi.x, pi.y, pi.z);
						pi.c.r += pointlight.applyLight(mat, view_vector, pi.n, v).r;
						pi.c.g += pointlight.applyLight(mat, view_vector, pi.n, v).g;
						pi.c.b += pointlight.applyLight(mat, view_vector, pi.n, v).b;
					}

					if(infinte && light) {
						pi.c.r += infinitelight.applyLight(mat, view_vector, pi.n).r;
						pi.c.g += infinitelight.applyLight(mat, view_vector, pi.n).g;
						pi.c.b += infinitelight.applyLight(mat, view_vector, pi.n).b;
					}
					pi.c.r = (float) Math.min(1.0, pi.c.r);
					pi.c.g = (float) Math.min(1.0, pi.c.g);
					pi.c.b = (float) Math.min(1.0, pi.c.b);

					drawDepthPoint(buff,pi,depthbuffer);
					interploation_z-=invslope_z;
				}

			// calculate the pa.x and pb.x on next line
			curx1 -= invslope1;
			curx2 -= invslope2;
			curz1 -= invslope_z1;
			curz2 -= invslope_z2;
		}

		// define the similar value like above for drawing lower triangle
		curx1 = v3.x;
		curx2 = v3.x;
		curz1 = v3.z;
		curz2 = v3.z;

		invslope1 = (float)(v2.x - v3.x) / (float)(v2.y - v3.y);
		invslope2 = (float)(v1.x - v3.x) / (float)(v1.y - v3.y);
		invslope_z1 =  (float)(v2.z - v3.z)/(float)(v2.y - v3.y);
		invslope_z2 =  (float)(v1.z - v3.z)/(float)(v1.y - v3.y);

		// drawing lower triangle
		for (scanlineY = v3.y; scanlineY <= v2.y; scanlineY++)
		{
			// calculate pa's color and pb's color
			normala.x = normal3.x*((float)(v2.y - scanlineY)/(float)(v2.y - v3.y)) + normal2.x*((float)(scanlineY - v3.y)/(float)(v2.y - v3.y));
			normala.y = normal3.y*((float)(v2.y - scanlineY)/(float)(v2.y - v3.y)) + normal2.y*((float)(scanlineY - v3.y)/(float)(v2.y - v3.y));
			normala.z = normal3.z*((float)(v2.y - scanlineY)/(float)(v2.y - v3.y)) + normal2.z*((float)(scanlineY - v3.y)/(float)(v2.y - v3.y));

			normala.x = normal3.x*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + normal1.x*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			normala.y = normal3.y*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + normal1.y*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			normala.z = normal3.z*((float)(v1.y - scanlineY)/(float)(v1.y - v3.y)) + normal1.z*((float)(scanlineY - v3.y)/(float)(v1.y - v3.y));


			Point2D_depth pa = new Point2D_depth((int)curx1,scanlineY,(int)curz1,normala);
			Point2D_depth pb = new Point2D_depth((int)curx2,scanlineY,(int)curz2,normalb);

			float interploation_z = pa.z;
			float invslope_z =(float)(pb.z - pa.z)/(float)(pb.x - pa.x);

			if(pa.x<pb.x)
				for(int i = pa.x; i <= pb.x;i++)
				{

						// calculate the color of point by using bilinear interpolation method
					normal.x = normala.x*((float)(pb.x - i)/(float)(pb.x - pa.x)) + normalb.x*((float)(i - pa.x)/(float)(pb.x - pa.x));
					normal.y = normala.y*((float)(pb.x - i)/(float)(pb.x - pa.x)) + normalb.y*((float)(i - pa.x)/(float)(pb.x - pa.x));
					normal.z = normala.z*((float)(pb.x - i)/(float)(pb.x - pa.x)) + normalb.z*((float)(i - pa.x)/(float)(pb.x - pa.x));

					//draw point
					Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,normal);

					pi.c.setR_int(0);pi.c.setG_int(0);pi.c.setB_int(0);
					if(amb && light) {
						pi.c.r += ambienlight.applyLight(mat).r;
						pi.c.g += ambienlight.applyLight(mat).g;
						pi.c.b += ambienlight.applyLight(mat).b;
					}

					if(point && light) {
						Vector3D v = new Vector3D(pi.x, pi.y, pi.z);
						pi.c.r += pointlight.applyLight(mat, view_vector, pi.n, v).r;
						pi.c.g += pointlight.applyLight(mat, view_vector, pi.n, v).g;
						pi.c.b += pointlight.applyLight(mat, view_vector, pi.n, v).b;
					}

					if(infinte && light) {
						pi.c.r += infinitelight.applyLight(mat, view_vector, pi.n).r;
						pi.c.g += infinitelight.applyLight(mat, view_vector, pi.n).g;
						pi.c.b += infinitelight.applyLight(mat, view_vector, pi.n).b;
					}
					pi.c.r = (float) Math.min(1.0, pi.c.r);
					pi.c.g = (float) Math.min(1.0, pi.c.g);
					pi.c.b = (float) Math.min(1.0, pi.c.b);

					drawDepthPoint(buff,pi,depthbuffer);
					interploation_z+=invslope_z;
				}
			if(pa.x>pb.x)
				for(int i = pa.x; i >= pb.x;i--)
				{

						// calculate the color of point by using bilinear interpolation method
					normal.x = normala.x*((float)(i - pb.x)/(float)(pa.x - pb.x)) + normalb.x*((float)(pa.x - i)/(float)(pa.x - pb.x));
					normal.y = normala.y*((float)(i - pb.x)/(float)(pa.x - pb.x)) + normalb.y*((float)(pa.x - i)/(float)(pa.x - pb.x));
					normal.z = normala.z*((float)(i - pb.x)/(float)(pa.x - pb.x)) + normalb.z*((float)(pa.x - i)/(float)(pa.x - pb.x));

					//draw point
					Point2D_depth pi = new Point2D_depth(i,scanlineY,(int)interploation_z,normal);

					pi.c.setR_int(0);pi.c.setG_int(0);pi.c.setB_int(0);
					if(amb && light) {
						pi.c.r += ambienlight.applyLight(mat).r;
						pi.c.g += ambienlight.applyLight(mat).g;
						pi.c.b += ambienlight.applyLight(mat).b;
					}

					if(point && light) {
						Vector3D v = new Vector3D(pi.x, pi.y, pi.z);
						pi.c.r += pointlight.applyLight(mat, view_vector, pi.n, v).r;
						pi.c.g += pointlight.applyLight(mat, view_vector, pi.n, v).g;
						pi.c.b += pointlight.applyLight(mat, view_vector, pi.n, v).b;
					}

					if(infinte && light) {
						pi.c.r += infinitelight.applyLight(mat, view_vector, pi.n).r;
						pi.c.g += infinitelight.applyLight(mat, view_vector, pi.n).g;
						pi.c.b += infinitelight.applyLight(mat, view_vector, pi.n).b;
					}
					pi.c.r = (float) Math.min(1.0, pi.c.r);
					pi.c.g = (float) Math.min(1.0, pi.c.g);
					pi.c.b = (float) Math.min(1.0, pi.c.b);

					drawDepthPoint(buff,pi,depthbuffer);
					interploation_z-=invslope_z;
				}

			// calculate the pa.x and pb.x on next line
			curx1 += invslope1;
			curx2 += invslope2;
			curz1 += invslope_z1;
			curz2 += invslope_z2;
		}
	}

	/**********************************************************************
	 * Helper function to bubble sort triangle vertices by ascending x value.
	 * 
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @return 
	 *          Array of 3 points, sorted by ascending x value.
	 */
	private static Point2D_depth[] sortTriangleVerts(Point2D_depth p1, Point2D_depth p2, Point2D_depth p3)
	{
	    Point2D_depth pts[] = {p1, p2, p3};
	    Point2D_depth tmp;
	    int j=0;
	    boolean swapped = true;
	         
	    while (swapped) 
	    {
	    	swapped = false;
	    	j++;
	    	for (int i = 0; i < 3 - j; i++) 
	    	{                                       
	    		if (pts[i].x > pts[i + 1].x) 
	    		{                          
	    			tmp = pts[i];
	    			pts[i] = pts[i + 1];
	    			pts[i + 1] = tmp;
	    			swapped = true;
	    		}
	    	}                
	    }
	    return(pts);
	}
}



/*
	public static void drawPhongShadingLine(Material mat, BufferedImage buff, Point3D p1, Point3D p2, Vector3D n0, Vector3D n1, CombinedLight light, float[][] depthBuffer, Vector3D view_vector)
	{
		int x0=p1.x, y0=p1.y, z0 = p1.z;
		int xEnd=p2.x, yEnd=p2.y, zEnd = p2.z;
		int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0);

		float dz = p2.z - p1.z;

		float steps;

		if (Math.abs(dx) > Math.abs(dy)) {
			steps = Math.abs(dx);
		} else {
			steps = Math.abs(dy);
		}

		float zInc = dz / steps;

		if(dx==0 && dy==0)
		{
			drawPoint(buff, p1, depthBuffer);
			return;
		}


		// if slope is greater than 1, then swap the role of x and y
		boolean x_y_role_swapped = (dy > dx);
		if(x_y_role_swapped)
		{
			x0=(int)p1.y;
			y0=(int)p1.x;

			xEnd=(int)p2.y;
			yEnd=(int)p2.x;

			dx = Math.abs(xEnd - x0);
			dy = Math.abs(yEnd - y0);
		}

		Point3D pk = new Point3D(p1.x, p1.y, p1.z, p1.c);

		// initialize the decision parameter and increments
		int p = 2 * dy - dx;
		int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
		int x=x0, y=y0;

		float z = pk.z;

		// set step increment to be positive or negative
		int step_x = x0<xEnd ? 1 : -1;
		int step_y = y0<yEnd ? 1 : -1;

		// deal with setup for color interpolation
		// first get r,g,b integer values at the end points
		float r0= n0.x, rEnd= n1.x;
		float g0= n0.y, gEnd= n1.y;
		float b0= n0.z, bEnd= n1.z;

		// compute the change in r,g,b
		float dr=Math.abs(rEnd-r0), dg=Math.abs(gEnd-g0), db=Math.abs(bEnd-b0);

		// set step increment to be positive or negative
		int step_r = r0<rEnd ? 1 : -1;
		int step_g = g0<gEnd ? 1 : -1;
		int step_b = b0<bEnd ? 1 : -1;

		// compute whole step in each color that is taken each time through loop
		float whole_step_r = step_r*(dr/dx);
		float whole_step_g = step_g*(dg/dx);
		float whole_step_b = step_b*(db/dx);

		// compute remainder, which will be corrected depending on decision parameter
		dr=dr%dx;
		dg=dg%dx;
		db=db%dx;

		// initialize decision parameters for red, green, and blue
		float p_r = 2 * dr - dx;
		float twoDr = 2 * dr,  twoDrMinusDx = 2 * (dr - dx);
		float r=r0;

		float p_g = 2 * dg - dx;
		float twoDg = 2 * dg,  twoDgMinusDx = 2 * (dg - dx);
		float g=g0;

		float p_b = 2 * db - dx;
		float twoDb = 2 * db,  twoDbMinusDx = 2 * (db - dx);
		float b=b0;

		// draw start pixel
		if(x_y_role_swapped)
		{
			if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()){
				if(depthBuffer[y][x] < z){
					Vector3D normal = new Vector3D((float)r, (float)g, (float)b);
					ColorType color = new ColorType();

					color = light.applyLight(mat, view_vector, normal, new Vector3D(y, x, z));

					int rr = color.getR_int();
					int gg = color.getG_int();
					int bb = color.getB_int();
					buff.setRGB(y, buff.getHeight()-x-1, (rr<<16) | (gg<<8) | bb);
					depthBuffer[y][x] = z;
				}
			}
		}
		else
		{
			if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()){
				if(depthBuffer[x][y] < z){
					Vector3D normal = new Vector3D((float)r, (float)g, (float)b);
					ColorType color = new ColorType();
					color = light.applyLight(mat, view_vector, normal, new Vector3D(x,y,z));
					int rr = color.getR_int();
					int gg = color.getG_int();
					int bb = color.getB_int();
					//real r,g and b values are obtained for the pixel
					buff.setRGB(x, buff.getHeight()-y-1, (rr<<16) | (gg<<8) | bb);
					depthBuffer[x][y]=z;

				}
			}
		}

		while (x != xEnd)
		{
			// increment x and y
			x+=step_x;
			z+=zInc;
			pk.z = Math.round(z);
			if (p < 0)
				p += twoDy;
			else
			{
				y+=step_y;
				p += twoDyMinusDx;
			}

			// increment r by whole amount slope_r, and correct for accumulated error if needed
			r+=whole_step_r;
			if (p_r < 0)
				p_r += twoDr;
			else
			{
				r+=step_r;
				p_r += twoDrMinusDx;
			}

			// increment g by whole amount slope_b, and correct for accumulated error if needed
			g+=whole_step_g;
			if (p_g < 0)
				p_g += twoDg;
			else
			{
				g+=step_g;
				p_g += twoDgMinusDx;
			}

			// increment b by whole amount slope_b, and correct for accumulated error if needed
			b+=whole_step_b;
			if (p_b < 0)
				p_b += twoDb;
			else
			{
				b+=step_b;
				p_b += twoDbMinusDx;
			}

			if(x_y_role_swapped)
			{
				if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()){
					if(depthBuffer[y][x] < z){
						Vector3D normal = new Vector3D((float)r, (float)g, (float)b);
						ColorType color = new ColorType();

						color = light.applyLight(mat, view_vector, normal, new Vector3D(y, x, z));
						//color interpolation
						int rr = color.getR_int();
						int gg = color.getG_int();
						int bb = color.getB_int();
						buff.setRGB(y, buff.getHeight()-x-1, (rr<<16) | (gg<<8) | bb);
						depthBuffer[y][x] = z;
					}
				}
			}
			else
			{
				if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()){
					if(depthBuffer[x][y] < z){
						Vector3D normal = new Vector3D((float)r, (float)g, (float)b);
						ColorType color = new ColorType();
						color = light.applyLight(mat, view_vector, normal, new Vector3D(x,y,z));
						int rr = color.getR_int();
						int gg = color.getG_int();
						int bb = color.getB_int();
						//real r,g and b values are obtained for the pixel
						buff.setRGB(x, buff.getHeight()-y-1, (rr<<16) | (gg<<8) | bb);
						depthBuffer[x][y]=z;
					}
				}
			}
		}
	}

	public static void drawTrianglePhong(Material mat, BufferedImage buff, Point3D tri, Point3D tri2, Point3D tri3, Vector3D n0, Vector3D n1, Vector3D n2, float[][] depthBuffer, boolean do_smooth, boolean phong, CombinedLight combined, Vector3D view_vector)
	{
		tri.n = n0;
		tri2.n = n1;
		tri3.n = n2;
		// sort the triangle vertices by ascending x value
		Point3D p[] = sortTriangleVerts(tri,tri2,tri3);

		p[0].n = n0;
		p[1].n = n1;
		p[2].n = n2;

		int x;
		float y_a, y_b;
		float dy_a, dy_b;
		float dr_a=0, dg_a=0, db_a=0, dr_b=0, dg_b=0, db_b=0;

		Point3D side_a = new Point3D(p[0]), side_b = new Point3D(p[0]);

		side_a.n = new Vector3D(p[0].n);
		side_b.n = new Vector3D(p[0].n);

		side_a.n.normalize();
		side_b.n.normalize();

		y_b = p[0].y;
		dy_b = ((float)(p[2].y - p[0].y))/(p[2].x - p[0].x);


		// calculate slopes in r, g, b for segment b
		dr_b = ((float)(p[2].n.x - p[0].n.x))/(p[2].x - p[0].x);
		dg_b = ((float)(p[2].n.y - p[0].n.x))/(p[2].x - p[0].x);
		db_b = ((float)(p[2].n.z - p[0].n.z))/(p[2].x - p[0].x);


		// if there is a left-hand part to the triangle then fill it
		if(p[0].x != p[1].x)
		{
			y_a = p[0].y;
			dy_a = ((float)(p[1].y - p[0].y))/(p[1].x - p[0].x);

			// calculate slopes in r, g, b for segment a
			dr_a = ((float)(p[1].n.x - p[0].n.x))/(p[1].x - p[0].x);
			dg_a = ((float)(p[1].n.y - p[0].n.y))/(p[1].x - p[0].x);
			db_a = ((float)(p[1].n.z - p[0].n.z))/(p[1].x - p[0].x);

			// loop over the columns for left-hand part of triangle
			// filling from side a to side b of the span
			for(x = p[0].x; x < p[1].x; ++x)
			{
				drawLinePhong(mat, buff, side_a, side_b, side_a.n, side_b.n,combined, depthBuffer, view_vector);

				++side_a.x;
				++side_b.x;
				y_a += dy_a;
				y_b += dy_b;
				side_a.y = (int)y_a;
				side_b.y = (int)y_b;
				side_a.n.x +=dr_a;
				side_b.n.x +=dr_b;
				side_a.n.y +=dg_a;
				side_b.n.y +=dg_b;
				side_a.n.z +=db_a;
				side_b.n.z +=db_b;

				side_a.n.normalize();
				side_b.n.normalize();
			}
		}

		// there is no right-hand part of triangle
		if(p[1].x == p[2].x)
			return;

		// set up to fill the right-hand part of triangle
		// replace segment a
		side_a = new Point3D(p[1]);
		side_a.n = new Vector3D(p[1].n);

		y_a = p[1].y;
		dy_a = ((float)(p[2].y - p[1].y))/(p[2].x - p[1].x);

		// calculate slopes in r, g, b for replacement for segment a
		dr_a = ((float)(p[2].n.x - p[1].n.x))/(p[2].x - p[1].x);
		dg_a = ((float)(p[2].n.y - p[1].n.y))/(p[2].x - p[1].x);
		db_a = ((float)(p[2].n.z - p[1].n.z))/(p[2].x - p[1].x);


		// loop over the columns for right-hand part of triangle
		// filling from side a to side b of the span
		for(x = p[1].x; x <= p[2].x; ++x)
		{
			drawLinePhong(mat, buff, side_a, side_b, side_a.n, side_b.n, combined, depthBuffer, view_vector);


			++side_a.x;
			++side_b.x;
			y_a += dy_a;
			y_b += dy_b;
			side_a.y = (int)y_a;
			side_b.y = (int)y_b;

			side_a.n.x +=dr_a;
			side_b.n.x +=dr_b;
			side_a.n.y +=dg_a;
			side_b.n.y +=dg_b;
			side_a.n.z +=db_a;
			side_b.n.z +=db_b;

			side_a.n.normalize();
			side_b.n.normalize();

		}
	}
}

*/