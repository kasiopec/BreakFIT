package com.exploreca.tourfinder.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import android.content.Context;
import android.util.Log;

import com.exploreca.tourfinder.R;
import com.exploreca.tourfinder.model.Entertainment;

public class EntertainmentJDOMParser {

	private static final String LOGTAG = "EXPLORECA";

	private static final String TOUR_TAG = "tour";
	private static final String TOUR_ID = "tourId";
	private static final String TOUR_TITLE = "tourTitle";
	private static final String TOUR_DESC = "description";
	private static final String TOUR_PRICE = "price";
	private static final String TOUR_IMAGE = "image";
	

	public List<Entertainment> parseXML(Context context) {

		InputStream stream = context.getResources().openRawResource(R.raw.tours);
		SAXBuilder builder = new SAXBuilder();
		List<Entertainment> tours = new ArrayList<Entertainment>();

		try {

			Document document = (Document) builder.build(stream);
			org.jdom2.Element rootNode = document.getRootElement();
			List<org.jdom2.Element> list = rootNode.getChildren(TOUR_TAG);

			for (Element node : list) {
				Entertainment entertainments = new Entertainment();
				entertainments.setId(Integer.parseInt(node.getChildText(TOUR_ID)));
				entertainments.setTitle(node.getChildText(TOUR_TITLE));
				entertainments.setDescription(node.getChildText(TOUR_DESC));
				entertainments.setPrice(node.getChildText(TOUR_PRICE));
				entertainments.setImage(node.getChildText(TOUR_IMAGE));
				tours.add(entertainments);
			}

		} catch (IOException e) {
			Log.i(LOGTAG, e.getMessage());
		} catch (JDOMException e) {
			Log.i(LOGTAG, e.getMessage());
		}
		return tours;
	}

}
