package model.world;
import java.awt.*;
import java.util.Random;
public class Cover {
	private	int currentHP;
	private	Point location;
	public int getCurrentHP() {
		return currentHP;
	}
	public void setCurrentHP(int currentHP) {
		if(currentHP>=0)
		this.currentHP = currentHP;
	}
	public Point getLocation() {
		return location;
	}
public Cover(int x, int y){
	this.location= new Point(x,y);
	Random r=new Random();
	this.currentHP=r.nextInt(900)+100;
	}
}
