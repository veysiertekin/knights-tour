package com.veysiertekin.satranc;

import com.veysiertekin.satranc.ui.UI;

public class Start {
	UI ui;

	Start(int size) {
		if (size == 0)
			ui = new UI(8);
		else
			ui = new UI(size);
		ui.setVisible(true);
	}

	public static void main(String[] args) throws CloneNotSupportedException, InterruptedException {
		int size = 0;
		for (int i = 0; i < args.length; i++) {
			String string = args[i];
			if (string.equals("-size"))
				size = Integer.parseInt(args[i + 1]);
		}
		new Start(size);
	}
}
