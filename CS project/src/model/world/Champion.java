package model.world;
import java.util.*;

import model.abilities.*;
import model.effects.*;

import java.awt.*;
public class Champion implements Comparable {
private	String name;
private	int maxHP;
private	int currentHP;
private	int mana;
private	int maxActionPointsPerTurn;
private	int currentActionPoints;
private	int attackRange;
private	int attackDamage;
private	int speed;
private	ArrayList<Ability> abilities;
private	ArrayList<Effect> appliedEffects;
private	Condition condition;
private	Point location;

public Champion(String name, int maxHP, int mana, int maxActions, int speed, int attackRange,
		int attackDamage){
this.name=name;
this.maxHP=maxHP;
this.currentHP=maxHP;
this.currentActionPoints=maxActions;
this.mana=mana;
this.maxActionPointsPerTurn=maxActions;
this.speed=speed;
this.attackRange=attackRange;
this.attackDamage=attackDamage;
this.condition=Condition.ACTIVE;
this.abilities=new ArrayList<>();
this.appliedEffects=new ArrayList<>();
}
public int getCurrentHP() {
	return currentHP;
}
public void setCurrentHP(int currentHP) {
	if(currentHP>0 && currentHP<maxHP){
	this.currentHP = currentHP;}
	else{if(currentHP<=0){
		this.currentHP=0;
		this.condition=Condition.KNOCKEDOUT;}
	else{if(currentHP>maxHP){
	
	}this.currentHP=maxHP;
		}
	}
	}

public int getMaxActionPointsPerTurn() {
	return maxActionPointsPerTurn;
}
public void setCurrentActionPoints(int currentActionPoints) {
	this.currentActionPoints = currentActionPoints;
}
public void setMaxActionPointsPerTurn(int maxActionPointsPerTurn) {
	this.maxActionPointsPerTurn = maxActionPointsPerTurn;
}
public int getAttackDamage() {
	return attackDamage;
}
public void setAttackDamage(int attackDamage) {
	this.attackDamage = attackDamage;
}
public int getSpeed() {
	return speed;
}
public void setSpeed(int speed) {
	this.speed = speed;
}
public Point getLocation() {
	return location;
}
public void setLocation(Point location) {
	this.location = location;
}
public String getName() {
	return name;
}
public int getMaxHP() {
	return maxHP;
}
public int getMana() {
	return mana;
}
public int getCurrentActionPoints() {
	return currentActionPoints;
}
public int getAttackRange() {
	return attackRange;
}
public ArrayList<Ability> getAbilities() {
	return abilities;
}
public ArrayList<Effect> getAppliedEffects() {
	return appliedEffects;
}
public Condition getCondition() {
	return condition;
}
public void setCondition(Condition condition) {
	this.condition = condition;
}
public void setMana(int mana) {
	if(mana>0){
	this.mana = mana;}
	else{
		this.mana=0;
		this.condition=Condition.INACTIVE;
	}
}

public int compareTo(Object o) {
	Champion c=(Champion) o;
	if(c.getSpeed()>this.getSpeed()){
		return 1;
	}
	if(c.getSpeed()<this.getSpeed()){
		return -1;
	}
	return 0;
}


}
