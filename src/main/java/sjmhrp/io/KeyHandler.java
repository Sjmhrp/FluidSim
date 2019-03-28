package sjmhrp.io;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyHandler {

	public static boolean[] keys;
	public static boolean[] buttons;
	
	public static void init() {
		keys = new boolean[Keyboard.KEYBOARD_SIZE];
		buttons = new boolean[Mouse.getButtonCount()];
	}
	
	public static void tick() {
		if(keys==null||buttons==null)return;
		while(Keyboard.next())keys[Keyboard.getEventKey()]=Keyboard.getEventKeyState();
		while(Mouse.next()) {
			int button = Mouse.getEventButton();
			if(button<0)continue;
			buttons[button]=Mouse.getEventButtonState();
		}
	}
}