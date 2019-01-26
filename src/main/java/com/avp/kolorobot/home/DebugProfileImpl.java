package com.avp.kolorobot.home;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DebugProfileImpl implements RuntimeProfile {

	@Override
	public boolean isProduction() {
		return false;
	}
}
