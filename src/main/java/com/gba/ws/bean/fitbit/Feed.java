
package com.gba.ws.bean.fitbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mohan
 * @createdOn Jan 12, 2018 12:22:58 PM
 */
public class Feed {

	@SerializedName("Harvard_IAQ")
	@Expose
	private HarvardIAQ harvardIAQ = new HarvardIAQ();

	public HarvardIAQ getHarvardIAQ() {
		return this.harvardIAQ;
	}

	public Feed setHarvardIAQ(HarvardIAQ harvardIAQ) {
		this.harvardIAQ = harvardIAQ;
		return this;
	}

}
