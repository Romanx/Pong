package common;

import java.io.Serializable;
/*
 * An Object in the game, represented as a rectangle
 *  Holds details of shape, plus possible direction of travel
 */
public class GameObject implements Serializable
{
  private static final long serialVersionUID = 1L;

  public enum Collision { HIT, NO_HIT };
  // All the variables below are vital to the state of the object
  private double topX = 0.0;          // Top left corner X
  private double topY = 0.0;          // Top left corner Y
  private double width = 0.0;         // Width of object
  private double height = 0.0;        // Height of object
  private double dirX = 1;            // Direction X (1 or -1)
  private double dirY = 1;            // Direction Y (1 or -1)

  /**
   * Create an instance of a Game object
   * @param x Top left hand corner x
   * @param y Top left hand corner y
   * @param aWidth Width of object
   * @param aHeight Height of Object
   */
  public GameObject( double x, double y, double aWidth, double aHeight )
  {
    topX = x; topY = y;
    width = aWidth; height = aHeight; 
  }
    
  public double getX()      { return topX; }
  public double getY()      { return topY; }
  public double getWidth()  { return width; }
  public double getHeight() { return height; }
 
  public void setX( double x ) { topX = x; }
  public void setY( double y ) { topY = y; }
 
  /**
   * Move object by X units
   *  The actual direction moved is flipped by changeDirectionX()
   * @param units Units to move game object by X direction
   */
  public void moveX( double units )
  {
    topX += units * dirX;
  }
  
  /**
   * Move object by Y units
   *   The actual direction moved is flipped by changeDirectionY()
   * @param units Units to move game object by Y direction
   */
  public void moveY( double units )
  {
    topY += units * dirY;
  }
  
  /**
   * Change direction of future moves in the X direction 
   */
  public void changeDirectionX()
  {
    dirX = -dirX;
  }
  
  /**
   * Change direction of future moves in the Y direction 
   */
  public void changeDirectionY()
  {
    dirY = -dirY;
  }
  
  /**
   * Detect a collision between two GameObjects 
   *  Would be good to know where the object is hit
   *  @param with Check if object (with) collides with us
   */
  public Collision collision( GameObject with )
  {
    if ( topX >= with.getX()+with.getWidth()  ||
         topX+width <= with.getX()            ||
         topY >= with.getY()+with.getHeight() ||
         topY+height <= with.getY() )
      return Collision.NO_HIT;
    else {
      return Collision.HIT;
    }
  }
  
}
