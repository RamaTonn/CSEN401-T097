package model.effects;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import engine.Game;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.GameActionException;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.Champion;
import model.world.Condition;


public abstract class Effect implements Cloneable {
	private String name;
	private EffectType type;
	private int duration;

	public Effect(String name, int duration, EffectType type) {
		this.name = name;
		this.type = type;
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public EffectType getType() {
		return type;
	}
	
	
	
	
	public void apply(Champion c) throws NotEnoughResourcesException, AbilityUseException,InvalidTargetException {
		ArrayList<Effect> list=c.getAppliedEffects();
		int size = list.size();
		int i =0;
		Effect effect;

		
		while(i<size) {
			effect =(Effect)list.remove(i);
			boolean flag = false ;
			
			if(effect.getName().equals("Disarm"))
			{
				for (Ability a : c.getAbilities()) {
		 			if (a.getName().equals("Punch")) 
		 				if(a.getCurrentCooldown()!=0)
		 				    flag = true;
		 				else a.setCurrentCooldown(1);
		 		}
				
		    if(c.getCurrentActionPoints()==0) 
		         throw  new NotEnoughResourcesException();
		    
		    else if(flag)
	 			throw new AbilityUseException();
			 	
		     else {
		      c.getAbilities().add(new DamagingAbility("Punch",0,1,1,AreaOfEffect.SINGLETARGET,1,50));
		     }
		}
			
			else if(effect.getName().equals("Dodge")) 
            {
		         
				c.setSpeed( (int)(c.getSpeed()+ (c.getSpeed()*0.05)));
			}
			else if(effect.getName().equals("Embrace")) 
            {
				c.setCurrentHP(c.getCurrentHP()+(int)(c.getMaxHP()*0.2));
			    c.setMana(c.getMana()+(int)(c.getMana()*0.2));
			    c.setSpeed( (int)(c.getSpeed()+ (c.getSpeed()*0.2)));
			    c.setAttackDamage((int)(c.getAttackDamage()+ (c.getAttackDamage()*0.2)));
			}
			else if(effect.getName().equals("Root")) 
            {
				c.setSpeed(0);
			}
			else if(effect.getName().equals("Silence")) 
            {
				
				c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+2);
				c.setCurrentActionPoints(c.getCurrentActionPoints()+2);
			}
			else if(effect.getName().equals("SpeedUp")) 
            {
				c.setSpeed( (int)(c.getSpeed()+ (c.getSpeed()*0.15)));
				c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+1);
				c.setCurrentActionPoints(c.getCurrentActionPoints()+1);
			}
			else if(effect.getName().equals("Stun")) 
            {
				c.setCondition(Condition.INACTIVE);		
			
            } 
			else if(effect.getName().equals("Shock")) 
            {
				c.setSpeed( (int)(c.getSpeed()- (c.getSpeed()*0.10)));
			    c.setAttackDamage((int)(c.getAttackDamage()-c.getAttackDamage()*10));
			    c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-1);
				c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
            } 
			else if(effect.getName().equals("Shield")) 
            {
				
				c.setSpeed((int)(c.getSpeed()+(c.getSpeed()*0.02)));
				
			  
            } 
			else if(effect.getName().equals("PowerUp")) 
            {			
				for (Ability a : c.getAbilities()) {				
					 if(a instanceof HealingAbility) {
						 HealingAbility h=(HealingAbility) a;
						 h.setHealAmount(h.getHealAmount()+(int)(h.getHealAmount()*0.2));
					 }
					 else if(a instanceof DamagingAbility) {
						DamagingAbility h=(DamagingAbility) a;
						h.setDamageAmount(h.getDamageAmount()+(int)(h.getDamageAmount()*0.2));
					 }				
		 		}
            } 
			i++;
			list.add(effect);
		}	
	}
	
	
}

