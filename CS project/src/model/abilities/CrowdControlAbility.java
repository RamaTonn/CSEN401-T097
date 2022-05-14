package model.abilities;

import java.util.ArrayList;

import model.effects.Effect;
import model.world.*;
import exceptions.*;

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

	public void execute(ArrayList<Damageable> targets) throws CloneNotSupportedException {
		for (Damageable c : targets) {
			Champion x = (Champion) c;
			this.effect.apply((Champion) c);
			x.getAppliedEffects().add((Effect) effect.clone());
		}
		this.setCurrentCooldown(this.getBaseCooldown());
	}
}
