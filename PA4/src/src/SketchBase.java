//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)

import java.awt.image.BufferedImage;
import java.util.*;

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
	public static void drawPoint(BufferedImage buff, Point3D p, float[][] depthBuffer)
	{
		float[][] depthBuffer1 = depthBuffer;
		if(p.x>=0 && p.x<buff.getWidth() && p.y>=0 && p.y < buff.getHeight()){
			if(depthBuffer1[p.x][p.y] < p.z){
				buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getRGB_int());
				depthBuffer1[p.x][p.y] = p.z;
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
	public static void drawLine(BufferedImage buff, Point3D p1, Point3D p2, float[][] depthBuffer)
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
	    	x0=p1.y; 
	    	y0=p1.x;

	    	xEnd=p2.y; 
	    	yEnd=p2.x;

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
	    	if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()){
	    		if(depthBuffer[y][x] < z){
	    			buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);
	    			depthBuffer[y][x] = z;
	    		}
	    	}
	    }
	    else
	    {
	    	if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()){
	    		if(depthBuffer[x][y] < z){
	    			buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);
	    			depthBuffer[x][y] = z;
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
	    			int tempp = buff.getHeight()-x-1;
	        		if(depthBuffer[y][x] < z){
	        			buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);
	        			depthBuffer[y][x] = z;
	        		
	        		}
	        		}
	    	}
	    	else
	    	{
	    		if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()){
	    			if(depthBuffer[x][y] < z){
	    				buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);
	    				depthBuffer[x][y] = z;
	    			}
	    	}
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
	 * @param tri
	 *          First given vertex of the triangle.
	 * @param tri2
	 *          Second given vertex of the triangle.
	 * @param tri3
	 *          Third given vertex of the triangle.
	 * @param do_smooth
	 *          Flag indicating whether flat fill or smooth fill should be used.                   
	 */
	public static void drawTriangle(BufferedImage buff, Point3D tri, Point3D tri2, Point3D tri3, float[][] depthBuffer, boolean do_smooth, boolean phong)
	{
	    // sort the triangle vertices by ascending x value
	    Point3D p[] = sortTriangleVerts(tri,tri2,tri3);    
	    int x; 
	    float y_a, y_b;
	    float dy_a, dy_b;
	    float dr_a=0, dg_a=0, db_a=0, dr_b=0, dg_b=0, db_b=0;
	    
	    Point3D side_a = new Point3D(p[0]), side_b = new Point3D(p[0]);
	    
	    if(!do_smooth)
	    {
	    	side_a.c = new ColorType(tri.c);
	    	side_b.c = new ColorType(tri.c);
	    }
	    
	    y_b = p[0].y;
	    dy_b = ((float)(p[2].y - p[0].y))/(p[2].x - p[0].x);
	    
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for segment b
	    	dr_b = ((float)(p[2].c.r - p[0].c.r))/(p[2].x - p[0].x);
	    	dg_b = ((float)(p[2].c.g - p[0].c.g))/(p[2].x - p[0].x);
	    	db_b = ((float)(p[2].c.b - p[0].c.b))/(p[2].x - p[0].x);
	    }
	    
	    // if there is a left-hand part to the triangle then fill it
	    if(p[0].x != p[1].x)
	    {
	    	y_a = p[0].y;
	    	dy_a = ((float)(p[1].y - p[0].y))/(p[1].x - p[0].x);
		    
	    	if(do_smooth)
	    	{
	    		// calculate slopes in r, g, b for segment a
	    		dr_a = ((float)(p[1].c.r - p[0].c.r))/(p[1].x - p[0].x);
	    		dg_a = ((float)(p[1].c.g - p[0].c.g))/(p[1].x - p[0].x);
	    		db_a = ((float)(p[1].c.b - p[0].c.b))/(p[1].x - p[0].x);
	    	}
		    
		    // loop over the columns for left-hand part of triangle
		    // filling from side a to side b of the span
		    for(x = p[0].x; x < p[1].x; ++x)
		    {
		    	
		    	drawLine(buff, side_a, side_b, depthBuffer);

		    	++side_a.x;
		    	++side_b.x;
		    	y_a += dy_a;
		    	y_b += dy_b;
		    	side_a.y = (int)y_a;
		    	side_b.y = (int)y_b;
		    	if(do_smooth)
		    	{
		    		side_a.c.r +=dr_a;
		    		side_b.c.r +=dr_b;
		    		side_a.c.g +=dg_a;
		    		side_b.c.g +=dg_b;
		    		side_a.c.b +=db_a;
		    		side_b.c.b +=db_b;
		    	}
		    }
	    }
	    
	    // there is no right-hand part of triangle
	    if(p[1].x == p[2].x)
	    	return;
	    
	    // set up to fill the right-hand part of triangle 
	    // replace segment a
	    side_a = new Point3D(p[1]);
	    if(!do_smooth)
	    	side_a.c =new ColorType(tri.c);
	    
	    y_a = p[1].y;
	    dy_a = ((float)(p[2].y - p[1].y))/(p[2].x - p[1].x);
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for replacement for segment a
	    	dr_a = ((float)(p[2].c.r - p[1].c.r))/(p[2].x - p[1].x);
	    	dg_a = ((float)(p[2].c.g - p[1].c.g))/(p[2].x - p[1].x);
	    	db_a = ((float)(p[2].c.b - p[1].c.b))/(p[2].x - p[1].x);
	    }

	    // loop over the columns for right-hand part of triangle
	    // filling from side a to side b of the span
	    for(x = p[1].x; x <= p[2].x; ++x)
	    {
	    	drawLine(buff, side_a, side_b, depthBuffer);
		    
	    	++side_a.x;
	    	++side_b.x;
	    	y_a += dy_a;
	    	y_b += dy_b;
	    	side_a.y = (int)y_a;
	    	side_b.y = (int)y_b;
	    	if(do_smooth)
	    	{
	    		side_a.c.r +=dr_a;
	    		side_b.c.r +=dr_b;
	    		side_a.c.g +=dg_a;
	    		side_b.c.g +=dg_b;
	    		side_a.c.b +=db_a;
	    		side_b.c.b +=db_b;
	    	}
	    }
	}

	/**********************************************************************
	 * Helper function to bubble sort triangle vertices by ascending x value.
	 * 
	 * @param tri
	 *          First given vertex of the triangle.
	 * @param tri2
	 *          Second given vertex of the triangle.
	 * @param tri3
	 *          Third given vertex of the triangle.
	 * @return 
	 *          Array of 3 points, sorted by ascending x value.
	 */
	private static Point3D[] sortTriangleVerts(Point3D tri, Point3D tri2, Point3D tri3)
	{
	    Point3D pts[] = {tri, tri2, tri3};
	    Point3D tmp;
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
	
	public static void drawLinePhong(Material mat, BufferedImage buff, Point3D p1, Point3D p2, Vector3D n0, Vector3D n1, CombinedLight light, float[][] depthBuffer, Vector3D view_vector)
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

