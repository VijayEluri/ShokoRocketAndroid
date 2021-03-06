package uk.danishcake.shokorocket.moding;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.danishcake.shokorocket.NL;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SkinProgress {
	private Set<String> mUnlockedSkins = new HashSet<String>();
	private HashMap<String, String> mDefaults = new HashMap<String, String>();
	private HashMap<String, String> mSkin = new HashMap<String, String>();
	private String mSkinName = "";
	private Context mContext = null;

	public SkinProgress(Context context) {
		SharedPreferences sp = context.getSharedPreferences("Unlocks",
				Context.MODE_PRIVATE);
		String unlocked_skins = sp.getString("UnlockedSkins", "");
		String[] unlocked_skins_list = unlocked_skins.split(";");
		for (int i = 0; i < unlocked_skins_list.length; i++) {
			if (unlocked_skins_list[i].length() > 0)
				mUnlockedSkins.add(unlocked_skins_list[i]);
		}
		mSkinName = sp.getString("SelectedSkin", "");
		mContext = context;
		try {
			loadSkins("Animations/DefaultAnimationFiles.xml", mDefaults);
			if(mSkinName.length() > 0)
				loadSkins(mSkinName, mSkin);
		} catch (IOException io_ex) {
			Log.e("SkinProgress", "Unable to load default animation files");
		}
	}

	private void loadSkins(String skin, HashMap<String, String> map)
			throws IOException {
		map.clear();
		try {
			javax.xml.parsers.DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			dbf.setValidating(false);
			dbf.setCoalescing(false);
			dbf.setExpandEntityReferences(false);
			javax.xml.parsers.DocumentBuilder dbuilder = dbf
					.newDocumentBuilder();

			Document document = dbuilder.parse(mContext.getAssets().open(skin));
			Element root = document.getDocumentElement();
			NodeList animation_files = NL.ElementsByTag(root, "Animation");
			for (int i = 0; i < animation_files.getLength(); i++) {
				Element animation = (Element)animation_files.item(i);
				String id = animation.getAttribute("id");
				String val = animation.getAttribute("val");
				map.put(id, val);
			}

		} catch (ParserConfigurationException parse_config_error) {
			throw new IOException("Unable to create parser to read XML: "
					+ parse_config_error.getMessage());
		} catch (SAXException sax_error) {
			throw new IOException("Unable to load level due to SAX exception: "
					+ sax_error.getMessage());
		} catch (InvalidParameterException xml_error) {
			throw new IOException(
					"Unable to load level due to XML parameter error : "
							+ xml_error.getMessage());
		}
	}

	public void unlockSkin(String skin) {
		mUnlockedSkins.add(skin);

		String unlocked_skins = "";
		Iterator<String> it = mUnlockedSkins.iterator();
		while (it.hasNext()) {
			String skin_it = (String) it.next();
			if (unlocked_skins.length() > 0)
				unlocked_skins = unlocked_skins + ";" + skin_it;
			else
				unlocked_skins = skin_it;
		}
		SharedPreferences sp = mContext.getSharedPreferences("Unlocks",
				Context.MODE_PRIVATE);
		sp.edit().putString("UnlockedSkins", unlocked_skins).commit();
	}

	public boolean getSkinUnlocked(String skin) {
		return mUnlockedSkins.contains(skin);
	}

	public void setSkin(String skin) {
		try {
			if(skin.equals(""))
				mSkin.clear();
			else
			{
				loadSkins(skin, mSkin);
			}
		} catch (IOException io_ex) {
			Log.e("SkinProgress", "Unable to load default animation files");
		}
		mSkinName = skin;
		SharedPreferences sp = mContext.getSharedPreferences("Unlocks",
				Context.MODE_PRIVATE);
		sp.edit().putString("SelectedSkin", mSkinName).commit();
	}
	
	public String getSkin() {return mSkinName;}

	public InputStream getAnimation(String name) {
		try
		{
			if(mSkin.containsKey(name))
			{
				Log.d("SkinProgress", "Skin override " + mSkin.get(name));
				return mContext.getAssets().open(mSkin.get(name));
			} else if(mDefaults.containsKey(name))
			{
				return mContext.getAssets().open(mDefaults.get(name));
			}
		} catch(IOException io_ex)
		{
			//Do nothing, logging will occur next
			Log.d("SkinProgress", "Skin load error: " + io_ex.getMessage());
		}
		
		Log.e("SkinProgress", "Failed loading animation \"" + name + "\"");
		return null;
	}
}
