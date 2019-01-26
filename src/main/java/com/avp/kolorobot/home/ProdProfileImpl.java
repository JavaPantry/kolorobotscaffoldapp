package com.avp.kolorobot.home;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")
public class ProdProfileImpl implements RuntimeProfile {

	@Override
	public boolean isProduction() {
		return true;
	}
}
