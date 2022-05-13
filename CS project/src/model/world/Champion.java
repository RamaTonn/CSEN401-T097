package model.world;

import java.awt.Point;
import java.util.ArrayList;

import engine.Game;
import engine.*;
import exceptions.AbilityUseException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.effects.Effect;

public abstract class Champion implements Damageable , Comparable{
	private String name;
	private int maxHP;
	private int currentHP;
	private int mana;
	private int maxActionPointsPerTurn;
	private int currentActionPoints;
	private int attackRange;
	private int attackDamage;
	private int speed;
	private ArrayList<Ability> abilities;
	private ArrayList<Effect> appliedEffects;
	private Condition condition;
	private Point location;
	

	public Champion(String name, int maxHP, int mana, int actions, int speed, int attackRange, int attackDamage) {
		this.name = name;
		this.maxHP = maxHP;
		this.mana = mana;
		this.currentHP = this.maxHP;
		this.maxActionPointsPerTurn = actions;
		this.speed = speed;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.condition = Condition.ACTIVE;
		this.abilities = new ArrayList<Ability>();
		this.appliedEffects = new ArrayList<Effect>();
		this.currentActionPoints=maxActionPointsPerTurn;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public String getName() {
		return name;
	}

	public void setCurrentHP(int hp) {

		if (hp < 0) {
			currentHP = 0;
			
		} 
		else if (hp > maxHP)
			currentHP = maxHP;
		else
			currentHP = hp;

	}

	
	public int getCurrentHP() {

		return currentHP;
	}

	public ArrayList<Effect> getAppliedEffects() {
		return appliedEffects;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
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

	public void setSpeed(int currentSpeed) {
		if (currentSpeed < 0)
			this.speed = 0;
		else
			this.speed = currentSpeed;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point currentLocation) throws UnallowedMovementException   {
		if(condition.equals(Condition.INACTIVE)||condition.equals(Condition.ROOTED))
		   throw new UnallowedMovementException();
		this.location = currentLocation;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public ArrayList<Ability> getAbilities() {
		return abilities;
	}

	public int getCurrentActionPoints() {
		return currentActionPoints;
	}

	public void setCurrentActionPoints(int currentActionPoints) {
		if(currentActionPoints>maxActionPointsPerTurn)
			currentActionPoints=maxActionPointsPerTurn;
		else 
			if(currentActionPoints<0)
			currentActionPoints=0;
		this.currentActionPoints = currentActionPoints;
	}

	public int getMaxActionPointsPerTurn() {
		return maxActionPointsPerTurn;
	}

	public void setMaxActionPointsPerTurn(int maxActionPointsPerTurn) {
		this.maxActionPointsPerTurn = maxActionPointsPerTurn;
	}

	@Override
	public int compareTo(Object o) {
	Champion champ =(Champion)o;
		if(this.getSpeed()< champ.getSpeed())
		      return -1;
		else if(this.getSpeed() > champ.getSpeed())
			return 1;
		else if(this.getSpeed() == champ.getSpeed())
			if(this.getName().compareTo(champ.getName())<0)
				return 1; 
			else if(this.getName().compareTo(champ.getName())>0)
				return -1;
	     return 0;
		}
abstract public void useLeaderAbility(ArrayList<Champion> targets) ;
	

public boolean equals(Object o){
	
	Champion c=(Champion) o;
	if(c.getName().equals(this.getName()) && c.getMaxHP()==this.getMaxHP() && this.getCurrentHP()==c.getCurrentHP() 
			&& c.getMana()==this.getMana() && c.getMaxActionPointsPerTurn()==this.getMaxActionPointsPerTurn() 
			&& c.getCurrentActionPoints()==this.getCurrentActionPoints() && c.getAttackRange()==this.getAttackRange()
			&& c.getAttackDamage()==this.getAttackDamage() && c.getSpeed()==this.getSpeed() && c.getAbilities().equals(this.getAbilities())
			&& c.getAppliedEffects().equals(this.getAppliedEffects()) && c.getCondition().equals(this.getCondition())){
		return true;
	}
	return false;
}
	}

