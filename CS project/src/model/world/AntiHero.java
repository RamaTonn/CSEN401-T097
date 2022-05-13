package model.world;
import java.util.*;
import model.effects.*;
import engine.Game;
import engine.Game.*;
public class AntiHero extends Champion {

	public AntiHero(String name, int maxHP, int maxMana, int actions, int speed, int attackRange, int attackDamage) {
		super(name, maxHP, maxMana, actions, speed, attackRange, attackDamage);

	}
	public void useLeaderAbility(ArrayList<Champion> targets){
		for(Champion c : targets){
		
			Stun s=new Stun(2);
			c.getAppliedEffects().add(s);
		
		}
		
	}
	
}
