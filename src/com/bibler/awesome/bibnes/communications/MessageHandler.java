package com.bibler.awesome.bibnes.communications;

import java.util.ArrayList;

public class MessageHandler implements Notifiable, Notifier {
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	public MessageHandler() {
		
	}
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}

	@Override
	public void takeNotice(String message, Object notifier) {
		for(Notifiable objectToNotify : objectsToNotify) {
			objectToNotify.takeNotice(message, notifier);
		}
		
	}

	@Override
	public void notify(String messageToSend) {
		// TODO Auto-generated method stub
		
	}

}
