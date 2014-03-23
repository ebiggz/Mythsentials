package com.mythicacraft.plugins.mythsentials.Store;

import com.alecgorge.minecraft.jsonapi.api.APIMethodName;
import com.alecgorge.minecraft.jsonapi.api.JSONAPICallHandler;
import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class PurchaseHandler implements JSONAPICallHandler {
	// if you return true here, you WILL handle the API call in the handle method.
	// will be called twice for every API call, once to test if the method exists and once on the actuall call

	public boolean willHandle(APIMethodName methodName) {
		if(methodName.matches("giveStoreItem")) {
			return true;
		}
		return false;
	}

	// the result of this method will be treated as the response to the API request, even if null is returned (some methods do return null)
	public Object handle(APIMethodName methodName, Object[] args) {
		if(methodName.matches("giveStoreItem")) {
			try {
				String itemName = (String) args[0];
				String player = (String) args[1];

				StoreManager sm = Mythsentials.getStoreManager();

				if(!sm.hasItem(itemName)) return false;

				sm.administerItemContents(sm.getItem(itemName), player);

				return true;

			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
}
