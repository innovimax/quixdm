package innovimax.quixproc.datamodel.in.json;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import innovimax.quixproc.datamodel.QuiXCharStream;
import innovimax.quixproc.datamodel.QuiXException;
import innovimax.quixproc.datamodel.event.AQuiXEvent;
import innovimax.quixproc.datamodel.in.AQuiXEventStreamReader;
import innovimax.quixproc.datamodel.in.AStreamSource;
import innovimax.quixproc.datamodel.in.QuiXEventStreamReader;
import innovimax.quixproc.datamodel.in.AStreamSource.JSONStreamSource;

public class JSONQuiXEventStreamReader extends AQuiXEventStreamReader {
	private final JsonFactory ifactory;
	private JsonParser iparser;
	public JSONQuiXEventStreamReader(JSONStreamSource current) {
		 this.ifactory = new JsonFactory();
		 //ifactory.s
		// todo
	}
	@Override
	protected AQuiXEvent load(AStreamSource current) throws QuiXException {
		return load(((AStreamSource.JSONStreamSource) current).asInputStream());
	}

	protected AQuiXEvent load(InputStream current) throws QuiXException {
		try {
			this.iparser = this.ifactory.createParser(current);
		} catch (JsonParseException e) {
			throw new QuiXException(e);
		} catch (IOException e) {
			throw new QuiXException(e);
		}		
		return AQuiXEvent.getStartJSON();
	}

	@Override
	protected AQuiXEvent process(CallBack callback) throws QuiXException {
		//System.out.println("process");
		try {
			if (callback.getState().equals(QuiXEventStreamReader.State.END_SOURCE)) {
				return callback.processEndSource();
			}
	while(true) {
				JsonToken token = this.iparser.nextToken();
				if (token == null) {
					callback.setState(QuiXEventStreamReader.State.END_SOURCE);
					return AQuiXEvent.getEndJSON();
				}
					
				switch (token) {
				case END_ARRAY:
					return AQuiXEvent.getEndArray();
				case END_OBJECT:
					return AQuiXEvent.getEndObject();
				case FIELD_NAME:
					return AQuiXEvent.getKeyName();
				case NOT_AVAILABLE:
					break;
				case START_ARRAY:
					return AQuiXEvent.getStartArray();
				case START_OBJECT:
					return AQuiXEvent.getStartObject();
				case VALUE_EMBEDDED_OBJECT:
					break;
				case VALUE_FALSE:
					return AQuiXEvent.getValueFalse();
				case VALUE_NULL:
					return AQuiXEvent.getValueNull();
				case VALUE_NUMBER_FLOAT:
					return AQuiXEvent.getValueNumber(0.0);
				case VALUE_NUMBER_INT:
					return AQuiXEvent.getValueNumber(0.0);					
				case VALUE_STRING:
					return AQuiXEvent.getValueString(QuiXCharStream.EMPTY);
				case VALUE_TRUE:
					return AQuiXEvent.getValueTrue();
				default:
					break;
				
				}
			throw new QuiXException("Unknown event " + token);
		}
		} catch (JsonParseException e) {
			throw new QuiXException(e);
		} catch (IOException e) {
			throw new QuiXException(e);
		}
		
	}

	@Override
	public void reinitialize(AStreamSource current) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
