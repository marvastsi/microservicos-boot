package com.marvastsi.mcrsrv.service;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {

	public boolean isValidInventory(String inventoryId) {
		return inventoryId != null;
	}
}
