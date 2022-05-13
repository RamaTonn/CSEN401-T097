package model.abilities;

import java.util.ArrayList;

import model.effects.*;
import model.effects.Effect;

import model.effects.PowerUp;
import model.world.*;

public class CrowdControlAbility extends Ability {
	private Effect effect;

	public CrowdControlAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area, int required,
			Effect effect) {
		super(name, cost, baseCoolDown, castRadius, area, required);
		this.effect = effect;

	}

	public Effect getEffect() {
		return effect;
	}

	public void execute(ArrayList<Damageable> targets) {

		for (Damageable c : targets) {

			try {
				if (effect.getType() == EffectType.BUFF) {
					if (effect.getName().equals("PowerUp"))
						((PowerUp) effect.clone()).apply((Champion) c);
					if (effect.getName().equals("Embrace"))
						((Embrace) effect.clone()).apply((Champion) c);
					if (effect.getName().equals("Embrace"))
						((Dodge) effect.clone()).apply((Champion) c);
					if (effect.getName().equals("Embrace"))
						((SpeedUp) effect.clone()).apply((Champion) c);
					if (effect.getName().equals("Embrace"))
						((Shield) effect.clone()).apply((Champion) c);
				} else {
					if (effect.getName().equals("Embrace"))
						((Disarm) effect.clone()).apply((Champion) c);
					if (effect.getName().equals("Embrace"))
						((Silence) effect.clone()).apply((Champion) c);
					if (effect.getName().equals("Embrace"))
						((Root) effect.clone()).apply((Champion) c);
					if (effect.getName().equals("Embrace"))
						((Shock) effect.clone()).apply((Champion) c);
					if (effect.getName().equals("Embrace"))
						((Stun) effect.clone()).apply((Champion) c);

				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		}
		this.setCurrentCooldown(this.getBaseCooldown());

	}
}
