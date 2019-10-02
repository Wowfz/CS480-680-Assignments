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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SketchBase 
{
	public SketchBase()
	{
		// deliberately left blank
	}
	
	// draw a point
	public static void drawPoint(BufferedImage buff, Point2D p)
	{
		buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getBRGUint8());
	}
	
	//////////////////////////////////////////////////
	//	Implement the following two functions
	//////////////////////////////////////////////////
	
	// draw a line segment
	public static void drawLine(BufferedImage buff, Point2D p1, Point2D p2)
	{
		// replace the following line with your implementation
		drawPoint(buff, p2);

		ColorType color1 = p1.c; // color of p1
		ColorType color2 = p2.c; // color of p2
		ColorType color = new ColorType(0.0f,0.0f,0.0f); // interpolation color
		int dx= 0, dy = 0; //distance during p1.x p2.x and p1.y p2.y
		dx = p2.x - p1.x; 
		dy = p2.y - p1.y;
		int y = p1.y,x = p1.x;
		int p, p0;
		
		// here divide the coordination system into 8 situations
		// situation 1: Math.abs(p2.y-p1.y)>=Math.abs(p2.x-p1.x) && (p1.y<p2.y) && (p1.x < p2.x)
		if(Math.abs(p2.y-p1.y)>=Math.abs(p2.x-p1.x) && (p1.y<p2.y) && (p1.x < p2.x))
		{
			p0 = 2*dx - dy; // calculate p0
			p = p0;
			for(int yi = p1.y; yi <= p2.y; yi++)
			{
				// calculate p
				if(p > 0)
					p = p + 2* dx - 2*dy;
				else
					p = p + 2*dx;
				
				// calculate color interpolation
				color.r = color2.r*((float)(yi-p1.y)/(float)(p2.y - p1.y)) + color1.r*((float)(p2.y - yi)/(float)(p2.y - p1.y));
				color.g = color2.g*((float)(yi-p1.y)/(float)(p2.y - p1.y)) + color1.g*((float)(p2.y - yi)/(float)(p2.y - p1.y));
				color.b = color2.b*((float)(yi-p1.y)/(float)(p2.y - p1.y)) + color1.b*((float)(p2.y - yi)/(float)(p2.y - p1.y));
					
				// judge if choose the upper pixel
				if(p>0)
				{ 
					x += 1;
					Point2D pk = new Point2D(x, yi, color);
					drawPoint(buff, pk);
				}
				else
				{
					Point2D pk = new Point2D(x, yi, color);
					drawPoint(buff, pk);
				}
			}
		}
		
		// situation 2: Math.abs(p2.y-p1.y)>=Math.abs(p2.x-p1.x) && (p1.y>=p2.y) && (p1.x < p2.x)
		else if(Math.abs(p2.y-p1.y)>=Math.abs(p2.x-p1.x) && (p1.y>=p2.y) && (p1.x < p2.x))
		{
			dy = -dy;
			p0 = 2*dx - dy;
			p = p0;
			for(int yi = p1.y; yi >= p2.y; yi--)
			{
				if(p > 0)
					p = p + 2* dx - 2*dy;
				else
					p = p + 2*dx;
				
				color.r = color2.r*((float)(p1.y - yi)/(float)(p1.y - p2.y)) + color1.r*((float)(yi - p2.y)/(float)(p1.y - p2.y));
				color.g = color2.g*((float)(p1.y - yi)/(float)(p1.y - p2.y)) + color1.g*((float)(yi - p2.y)/(float)(p1.y - p2.y));
				color.b = color2.b*((float)(p1.y - yi)/(float)(p1.y - p2.y)) + color1.b*((float)(yi - p2.y)/(float)(p1.y - p2.y));
							
				if(p>0)
				{ 
					x += 1;
					Point2D pk = new Point2D(x, yi, color);
					drawPoint(buff, pk);
				}
				else
				{
					Point2D pk = new Point2D(x, yi, color);
					drawPoint(buff, pk);
				}
			}
		}
		
		// situation 3: Math.abs(p2.y-p1.y)<Math.abs(p2.x-p1.x) && (p1.y<p2.y) && (p1.x < p2.x)
		else if(Math.abs(p2.y-p1.y)<Math.abs(p2.x-p1.x) && (p1.y<p2.y) && (p1.x < p2.x))
		{
			p0 = 2*dy-dx;
			p = p0;
			for(int xi = p1.x; xi <= p2.x; xi++)
			{
				if(p > 0)
					p = p + 2* dy - 2*dx;
				else
					p = p + 2*dy;
				

				color.r = color2.r*((float)(xi-p1.x)/(float)(p2.x - p1.x)) + color1.r*((float)(p2.x - xi)/(float)(p2.x - p1.x));
				color.g = color2.g*((float)(xi-p1.x)/(float)(p2.x - p1.x)) + color1.g*((float)(p2.x - xi)/(float)(p2.x - p1.x));
				color.b = color2.b*((float)(xi-p1.x)/(float)(p2.x - p1.x)) + color1.b*((float)(p2.x - xi)/(float)(p2.x - p1.x));
				
				if(p > 0)
				{ 
					y += 1;
					Point2D pk = new Point2D(xi, y, color);
					drawPoint(buff, pk);
				}
				else
				{
					Point2D pk = new Point2D(xi, y, color);
					drawPoint(buff, pk);
				}
			}
		}
		
		// situation 4: Math.abs(p2.y-p1.y)<Math.abs(p2.x-p1.x) && (p1.y>=p2.y) && (p1.x < p2.x
		else if(Math.abs(p2.y-p1.y)<Math.abs(p2.x-p1.x) && (p1.y>=p2.y) && (p1.x < p2.x))
		{
			dy = -dy;
			p0 = 2*dy-dx;
			p = p0;
			for(int xi = p1.x; xi <= p2.x; xi++)
			{
				if(p > 0)
					p = p + 2* dy - 2*dx;
				else
					p = p + 2*dy;
				
				color.r = color2.r*((float)(xi-p1.x)/(float)(p2.x - p1.x)) + color1.r*((float)(p2.x - xi)/(float)(p2.x - p1.x));
				color.g = color2.g*((float)(xi-p1.x)/(float)(p2.x - p1.x)) + color1.g*((float)(p2.x - xi)/(float)(p2.x - p1.x));
				color.b = color2.b*((float)(xi-p1.x)/(float)(p2.x - p1.x)) + color1.b*((float)(p2.x - xi)/(float)(p2.x - p1.x));
				
				if(p > 0)
				{ 
					y -= 1;
					Point2D pk = new Point2D(xi, y, color);
					drawPoint(buff, pk);
				}
				else
				{
					Point2D pk = new Point2D(xi, y, color);
					drawPoint(buff, pk);
				}
			}
		}
		
		// situation 5: Math.abs(p2.y-p1.y)>=Math.abs(p2.x-p1.x) && (p1.y<p2.y) && (p1.x >= p2.x)
		else if(Math.abs(p2.y-p1.y)>=Math.abs(p2.x-p1.x) && (p1.y<p2.y) && (p1.x >= p2.x))
		{
			dx = -dx;
			p0 = 2*dx - dy;
			p = p0;
			for(int yi = p1.y; yi <= p2.y; yi++)
			{
				if(p > 0)
					p = p + 2* dx - 2*dy;
				else
					p = p + 2*dx;
				
				color.r = color2.r*((float)(yi-p1.y)/(float)(p2.y - p1.y)) + color1.r*((float)(p2.y - yi)/(float)(p2.y - p1.y));
				color.g = color2.g*((float)(yi-p1.y)/(float)(p2.y - p1.y)) + color1.g*((float)(p2.y - yi)/(float)(p2.y - p1.y));
				color.b = color2.b*((float)(yi-p1.y)/(float)(p2.y - p1.y)) + color1.b*((float)(p2.y - yi)/(float)(p2.y - p1.y));
				
				if(p>0)
				{ 
					x -= 1;
					Point2D pk = new Point2D(x, yi, color);
					drawPoint(buff, pk);
				}
				else
				{
					Point2D pk = new Point2D(x, yi, color);
					drawPoint(buff, pk);
				}
			}
		}
		
		// situation 6: Math.abs(p2.y-p1.y)>=Math.abs(p2.x-p1.x) && (p1.y>=p2.y) && (p1.x >= p2.x)
		else if(Math.abs(p2.y-p1.y)>=Math.abs(p2.x-p1.x) && (p1.y>=p2.y) && (p1.x >= p2.x))
		{
			dx = -dx;
			dy = -dy;
			p0 = 2*dx - dy;
			p = p0;
			for(int yi = p1.y; yi >= p2.y; yi--)
			{
				if(p > 0)
					p = p + 2* dx - 2*dy;
				else
					p = p + 2*dx;
				
				color.r = color2.r*((float)(p1.y - yi)/(float)(p1.y - p2.y)) + color1.r*((float)(yi - p2.y)/(float)(p1.y - p2.y));
				color.g = color2.g*((float)(p1.y - yi)/(float)(p1.y - p2.y)) + color1.g*((float)(yi - p2.y)/(float)(p1.y - p2.y));
				color.b = color2.b*((float)(p1.y - yi)/(float)(p1.y - p2.y)) + color1.b*((float)(yi - p2.y)/(float)(p1.y - p2.y));
				
				if(p>0)
				{ 
					x -= 1;
					Point2D pk = new Point2D(x, yi, color);
					drawPoint(buff, pk);
				}
				else
				{
					Point2D pk = new Point2D(x, yi, color);
					drawPoint(buff, pk);
				}
			}
		}
		
		// situation 7: Math.abs(p2.y-p1.y)<Math.abs(p2.x-p1.x) && (p1.y<p2.y) && (p1.x >= p2.x)
		else if(Math.abs(p2.y-p1.y)<Math.abs(p2.x-p1.x) && (p1.y<p2.y) && (p1.x >= p2.x))
		{
			dx = -dx;
			p0 = 2*dy-dx;
			p = p0;
			for(int xi = p1.x; xi >= p2.x; xi--)
			{
				if(p > 0)
					p = p + 2* dy - 2*dx;
				else
					p = p + 2*dy;
				
				color.r = color2.r*((float)(p1.x - xi)/(float)(p1.x - p2.x)) + color1.r*((float)(xi - p2.x)/(float)(p1.x - p2.x));
				color.g = color2.g*((float)(p1.x - xi)/(float)(p1.x - p2.x)) + color1.g*((float)(xi - p2.x)/(float)(p1.x - p2.x));
				color.b = color2.b*((float)(p1.x - xi)/(float)(p1.x - p2.x)) + color1.b*((float)(xi - p2.x)/(float)(p1.x - p2.x));
				
				if(p > 0)
				{ 
					y += 1;
					Point2D pk = new Point2D(xi, y, color);
					drawPoint(buff, pk);
				}
				else
				{
					Point2D pk = new Point2D(xi, y, color);
					drawPoint(buff, pk);
				}
			}
		}
		
		// situation 8: Math.abs(p2.y-p1.y)<Math.abs(p2.x-p1.x) && (p1.y>=p2.y) && (p1.x >= p2.x)
		else if(Math.abs(p2.y-p1.y)<Math.abs(p2.x-p1.x) && (p1.y>=p2.y) && (p1.x >= p2.x))
		{
			dx = -dx;
			dy = -dy;
			p0 = 2*dy-dx;
			p = p0;
			for(int xi = p1.x; xi >= p2.x; xi--)
			{
				if(p > 0)
					p = p + 2* dy - 2*dx;
				else
					p = p + 2*dy;
				
				color.r = color2.r*((float)(p1.x - xi)/(float)(p1.x - p2.x)) + color1.r*((float)(xi - p2.x)/(float)(p1.x - p2.x));
				color.g = color2.g*((float)(p1.x - xi)/(float)(p1.x - p2.x)) + color1.g*((float)(xi - p2.x)/(float)(p1.x - p2.x));
				color.b = color2.b*((float)(p1.x - xi)/(float)(p1.x - p2.x)) + color1.b*((float)(xi - p2.x)/(float)(p1.x - p2.x));
				
				if(p > 0)
				{ 
					y -= 1;
					Point2D pk = new Point2D(xi, y, color);
					drawPoint(buff, pk);
				}
				else
				{
					Point2D pk = new Point2D(xi, y, color);
					drawPoint(buff, pk);
				}
			}
		}
	}
	
	// draw a triangle
	public static void drawTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth)
	{
		// replace the following line with your implementation
		drawPoint(buff, p3);

		Point2D v1 = p1, v2 = p2, v3 = p3;

		//sort v1, v2, v3 by their y value, v1 > v2 > v3
			if(v1.y<=v2.y)
			{
				Point2D temp = v1;
				v1 = v2;
				v2 = temp;
			}
			if(v2.y<=v3.y)
			{
				Point2D temp = v2;
				v2 = v3;
				v3 = temp;
			}
			if(v2.y >= v1.y)
			{
				Point2D temp = v1;
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
		  
		  //the current x value on line v1v2 and v1v3(point pa and pb)
		  float curx1 = v1.x;
		  float curx2 = v1.x;

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
			  Point2D pa = new Point2D((int)curx1,scanlineY,colora);
			  Point2D pb = new Point2D((int)curx2,scanlineY,colorb);
			  
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
					  Point2D pi = new Point2D(i,scanlineY,color);
					  drawPoint(buff,pi);
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
					  Point2D pi = new Point2D(i,scanlineY,color);
					  drawPoint(buff,pi);
				  }
				  
			  // calculate the pa.x and pb.x on next line
			  curx1 -= invslope1;
			  curx2 -= invslope2;
		  }
		  
		  // define the similar value like above for drawing lower triangle
		  curx1 = v3.x;
		  curx2 = v3.x;
		  invslope1 = (float)(v2.x - v3.x) / (float)(v2.y - v3.y);
		  invslope2 = (float)(v1.x - v3.x) / (float)(v1.y - v3.y);
		  
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
			  
			  Point2D pa = new Point2D((int)curx1,scanlineY,color);
			  Point2D pb = new Point2D((int)curx2,scanlineY,color);
			  
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
					  
					  Point2D pi = new Point2D(i,scanlineY,color);
					  drawPoint(buff,pi);
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
					  
					  Point2D pi = new Point2D(i,scanlineY,color);
					  drawPoint(buff,pi);
				  }
				  
			  // calculate the pa.x and pb.x on next line
			  curx1 += invslope1;
			  curx2 += invslope2;
		  }
	}

	
	/////////////////////////////////////////////////
	// for texture mapping (Extra Credit for CS680)
	/////////////////////////////////////////////////
	public static void triangleTextureMap(BufferedImage buff, BufferedImage texture, Point2D p1, Point2D p2, Point2D p3)
	{
		// replace the following line wi\th your implementation
		drawPoint(buff, p3);
		
		// calculate the height and width of the texture
		int h_texture = texture.getHeight(),w_texture = texture.getWidth();
	    
		Point2D v1 = p1, v2 = p2, v3 = p3;
		
		//sort p1, p2, p3 by their y value
		if(v1.y<=v2.y)
		{
			Point2D temp = v1;
			v1 = v2;
			v2 = temp;
		}
		if(v2.y<=v3.y)
		{
			Point2D temp = v2;
			v2 = v3;
			v3 = temp;
		}
		if(v2.y >= v1.y)
		{
			Point2D temp = v1;
			v1 = v2;
			v2 = temp;
		}
		
		Point2D x1 = v1,x2 = v2, x3 = v3;
		//sort p1, p2, p3 by their x value
		if(x1.x<=x2.x)
		{
			Point2D temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if(x2.x<=x3.x)
		{
			Point2D temp = x2;
			x2 = x3;
			x3 = temp;
		}
		if(x2.x >= x1.x)
		{
			Point2D temp = x1;
			x1 = x2;
			x2 = temp;
		}
		
		//the inverse of slope between v2, v1 and v3, v1
		float invslope1 = (float)(v2.x - v1.x) / (float)(v2.y - v1.y);
		float invslope2 = (float)(v3.x - v1.x) / (float)(v3.y - v1.y);
		
		  //the current x value on line v1v2 and v1v3(point pa and pb)
		float curx1 = v1.x;
		float curx2 = v1.x;

		// initialize u v u1 v1 u2 v2 u3 v3
		float u = 0.0f, v = 0.0f;
		float u_1 = 0.0f,v_1 = 1.0f,u_2 = 0.0f, v_2,u_3 = 0.0f,v_3 = 0.0f;
		float u11,v11,u22,v22;
		
		// calculate the middle value of u and v for p1 p2 p3 three points
		float ux2=(float)(x2.x - x3.x)/(float)(x1.x - x3.x);
		float uy2=(float)(v2.y - v3.y)/(float)(v1.y - v3.y);
		
		// v2.y is the middle value of
		v_2 = uy2;
		
		// calculate u1 u2 u3
		if(v1.x >= v2.x && v2.x >= v3.x)
		{
			u_1 = 1.0f;
			u_3 = 0.0f;
			
			u_2 = ux2;
		}
		if(v3.x >= v2.x && v2.x >= v1.x)
		{
			u_1 = 0.0f;
			u_3 = 1.0f;
			
			u_2 = ux2;
		}
		if(v2.x >= v1.x && v1.x >= v3.x)
		{
			u_2 = 1.0f;
			u_3 = 0.0f;
			
			u_1 = ux2;
		}
		if(v3.x >= v1.x && v1.x >= v2.x)
		{
			u_2 = 0.0f;
			u_3 = 1.0f;
			
			u_1 = ux2;
		}
		if(v1.x >= v3.x && v3.x >= v2.x)
		{
			u_1 = 1.0f;
			u_2 = 0.0f;
			
			u_3 = ux2;
		}
		if(v2.x >= v3.x && v3.x >= v1.x)
		{
			u_1 = 0.0f;
			u_2 = 1.0f;
			
			u_3 = ux2;
		}
		
		  float scanlinev = v_1;

		  
		  int scanlineY = v1.y;
		  ColorType colora = new ColorType(1.0f,0.0f,0.0f);
		  ColorType colorb = new ColorType(1.0f,0.0f,0.0f);
		  
		  for (scanlineY = v1.y; scanlineY >= v2.y; scanlineY--)
		  {
			  // calculate 1 u v of pa, pb (u11 v11 u22 v22) 
			  v11 = v_1 - ((v_1-v_2)*(float)(v1.y - scanlineY)/(float)(v1.y - v2.y));
			  v22 = v_1 - ((v_1-v_3)*(float)(v1.y - scanlineY)/(float)(v1.y - v3.y));
			  u11 = u_1 - ((u_1-u_2)*(float)(v1.y - scanlineY)/(float)(v1.y - v2.y));
			  u22 = u_1 - ((u_1-u_3)*(float)(v1.y - scanlineY)/(float)(v1.y - v3.y));
			  
			  Point2D pa = new Point2D((int)curx1,scanlineY,colora);
			  Point2D pb = new Point2D((int)curx2,scanlineY,colorb);
			  
			  if(pa.x<pb.x)
				  for(int i = pa.x; i <= pb.x;i++)
				  {
					  // calculate u v by using bilinear interpolation
					  u = u11 - (u11-u22)*(float)(i - pa.x)/(float)(pb.x - pa.x);
					  v = v22 + (v11-v22)*(float)(pb.x - i)/(float)(pb.x - pa.x);
					  
					  // calculate x y by using u and x
					  int x = (int)(u*(float)w_texture), y = (int)(v*(float)h_texture);
					  
					  // processing the coordinate out of bound
					  if(x>=w_texture)
					  {
						  while(x >= w_texture)
							  x -= w_texture;
					  }
					  if(x<0)
					  {
						  while(x < 0)
							  x += w_texture;
					  }
					  if(y>=h_texture)
					  {
						  while(y >= h_texture)
							  y -= h_texture;
					  }
					  if(y<0)
					  {
						  while(y < 0)
							  y += h_texture;
					  }
					  
					  // using the data of x y
					  Object data =texture.getRaster().getDataElements(x, y, null);// get the pixel at (u,v)

					  // get r g b value from texture mapping 
					  float red = texture.getColorModel().getRed(data)/255.0f;
			          float green = texture.getColorModel().getGreen(data)/255.0f;
			          float blue = texture.getColorModel().getBlue(data)/255.0f;
			          
					  ColorType color = new ColorType(red,green,blue);
					  
					  // calculate
					  Point2D pi = new Point2D(i,scanlineY,color);
					  drawPoint(buff,pi);
					  
				  }
			  
			  if(pa.x>=pb.x)
				  for(int i = pa.x; i >= pb.x;i--)
				  {	  
					  // calculate u v by using bilinear interpolation
					  u = u11 - (u11-u22)*(float)(i - pa.x)/(float)(pb.x - pa.x);
					  v = v22 + (v11-v22)*(float)(pb.x - i)/(float)(pb.x - pa.x);
					  
					  int x = (int)(u*(float)w_texture), y = (int)(v*(float)h_texture);
					
					  // processing the coordinate out of bound
					  if(x>=w_texture)
					  {
						  while(x >= w_texture)
							  x -= w_texture;
					  }
					  if(x<0)
					  {
						  while(x < 0)
							  x += w_texture;
					  }
					  if(y>=h_texture)
					  {
						  while(y >= h_texture)
							  y -= h_texture;
					  }
					  if(y<0)
					  {
						  while(y < 0)
							  y += h_texture;
					  }
					  
					  // using the data of x y
					  Object data =texture.getRaster().getDataElements(x, y, null);// get the pixel at (u,v)
					  
					  float red = texture.getColorModel().getRed(data)/255.0f;
			          float green = texture.getColorModel().getGreen(data)/255.0f;
			          float blue = texture.getColorModel().getBlue(data)/255.0f;
			          
					  ColorType color = new ColorType(red,green,blue);
					  
					  Point2D pi = new Point2D(i,scanlineY,color);
					  drawPoint(buff,pi);

				  }

			  // pa.x pb.x value in next line
			  curx1 -= invslope1;
			  curx2 -= invslope2;
			  
		  }
		  
		  curx1 = v3.x;
		  curx2 = v3.x;
		  invslope1 = (float)(v2.x - v3.x) / (float)(v2.y - v3.y);
		  invslope2 = (float)(v1.x - v3.x) / (float)(v1.y - v3.y);
		  
		  for (scanlineY = v3.y; scanlineY <= v2.y; scanlineY++)
		  {
			  // calculate 1 u v of pa, pb (u11 v11 u22 v22) 
			  v11 = v_3 + ((v_2-v_3)*(float)(scanlineY - v3.y)/(float)(v2.y - v3.y));
			  v22 = v_3 + ((v_1-v_3)*(float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			  u11 = u_3 + ((u_2-u_3)*(float)(scanlineY - v3.y)/(float)(v2.y - v3.y));
			  u22 = u_3 + ((u_1-u_3)*(float)(scanlineY - v3.y)/(float)(v1.y - v3.y));
			
			  Point2D pa = new Point2D((int)curx1,scanlineY,colora);
			  Point2D pb = new Point2D((int)curx2,scanlineY,colorb);
			  
			  if(pa.x<pb.x)
				  for(int i = pa.x; i <= pb.x;i++)
				  {
					  // calculate u v by using bilinear interpolation
					  u = u11 - (u11-u22)*(float)(i - pa.x)/(float)(pb.x - pa.x);
					  v = v22 - (v11-v22)*(float)(pb.x - i)/(float)(pb.x - pa.x);
					  
					  int x = (int)(u*(float)w_texture), y = (int)(v*(float)h_texture);
					  
					  // processing the coordinate out of bound
					  if(x>=w_texture)
					  {
						  while(x >= w_texture)
							  x -= w_texture;
					  }
					  if(x<0)
					  {
						  while(x < 0)
							  x += w_texture;
					  }
					  if(y>=h_texture)
					  {
						  while(y >= h_texture)
							  y -= h_texture;
					  }
					  if(y<0)
					  {
						  while(y < 0)
							  y += h_texture;
					  }

					  // using the data of x y
					  Object data =texture.getRaster().getDataElements(x, y, null);// get the pixel at (u,v)
					  
					  float red = texture.getColorModel().getRed(data)/255.0f;
			          float green = texture.getColorModel().getGreen(data)/255.0f;
			          float blue = texture.getColorModel().getBlue(data)/255.0f;
			          
					  ColorType color = new ColorType(red,green,blue);
					  
					  Point2D pi = new Point2D(i,scanlineY,color);
					  drawPoint(buff,pi);

				  }
			  if(pa.x>pb.x)
				  for(int i = pa.x; i >= pb.x;i--)
				  {
					  // calculate u v by using bilinear interpolation
					  u = u11 - (u11-u22)*(float)(i - pa.x)/(float)(pb.x - pa.x);
					  v = v22 - (v11-v22)*(float)(pb.x - i)/(float)(pb.x - pa.x);
					  
					  // processing the coordinate out of bound
					  int x = (int)(u*(float)w_texture), y = (int)(v*(float)h_texture);
					  if(x>=w_texture)
					  {
						  while(x >= w_texture)
							  x -= w_texture;
					  }
					  if(x<0)
					  {
						  while(x < 0)
							  x += w_texture;
					  }
					  if(y>=h_texture)
					  {
						  while(y >= h_texture)
							  y -= h_texture;
					  }
					  if(y<0)
					  {
						  while(y < 0)
							  y += h_texture;
					  }
					  
					  // using the data of x y
					  Object data =texture.getRaster().getDataElements(x, y, null);// get the pixel at (u,v)
					  
					  float red = texture.getColorModel().getRed(data)/255.0f;
			          float green = texture.getColorModel().getGreen(data)/255.0f; 
			          float blue = texture.getColorModel().getBlue(data)/255.0f;
			          
					  ColorType color = new ColorType(red,green,blue);
					  
					  Point2D pi = new Point2D(i,scanlineY,color);
					  drawPoint(buff,pi);
					  
				  }

			  // pa.x pb.x value in next line
			  curx1 += invslope1;
			  curx2 += invslope2;
		  }
	}
}
