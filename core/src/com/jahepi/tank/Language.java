package com.jahepi.tank;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class Language {

	private static Language self;

	private static final String TAG = "Language";
	private static final String path = "lang/";
	private ArrayMap<String, String> map;
	private String selectedLanguage;
	
	public static final String ENGLISH = "en";
	public static final String SPANISH = "es";
	
	private Language() {
		map = new ArrayMap<String, String>();
	}
	
	public static Language getInstance() {
		if (self == null) {
			self = new Language();
		}
		return self;
	}
	
	public void load(String lang) {
		
		selectedLanguage = lang;
		
		FileHandle file = null;
		if (lang.equals(ENGLISH)) {
			file = Gdx.files.internal(path + "en.xml");
		}
		if (lang.equals(SPANISH)) {
			file = Gdx.files.internal(path + "es.xml");
		}
		if (file != null) {
			XmlReader xmlReader = new XmlReader();
			try {
				Element element = xmlReader.parse(file);
				Array<Element> items = element.getChildrenByName("item");
				for (Element item : items) {
					Gdx.app.log(TAG, item.getAttribute("name"));
					Gdx.app.log(TAG, item.getText());
					map.put(item.getAttribute("name"), item.getText());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getSelectedLanguage() {
		return selectedLanguage;
	}

	public String get(String name) {
		return map.get(name);
	}
}
