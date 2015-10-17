package innovimax.quixproc.datamodel.in.rdf;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.jena.atlas.web.TypedInputStream;
import org.apache.jena.graph.Triple;

import innovimax.quixproc.datamodel.QuiXCharStream;
import innovimax.quixproc.datamodel.QuiXException;
import innovimax.quixproc.datamodel.event.AQuiXEvent;
import innovimax.quixproc.datamodel.in.AQuiXEventStreamReader;
import innovimax.quixproc.datamodel.in.AStreamSource;
import innovimax.quixproc.datamodel.in.AStreamSource.RDFStreamSource;
import innovimax.quixproc.datamodel.in.QuiXEventStreamReader.State;

public class RDFQuiXEventStreamReader extends AQuiXEventStreamReader {
	private PipedRDFIterator<Triple> iter;
	private final Queue<AQuiXEvent> buffer = new LinkedList<AQuiXEvent>();
	private ExecutorService executor;

	public RDFQuiXEventStreamReader() {
	}

	@Override
	protected AQuiXEvent load(AStreamSource current) throws QuiXException {
		return load((RDFStreamSource) current);
	}

	private AQuiXEvent load(RDFStreamSource source) throws QuiXException {
		// Create a PipedRDFStream to accept input and a PipedRDFIterator to
		// consume it
		// You can optionally supply a buffer size here for the
		// PipedRDFIterator, see the documentation for details about recommended
		// buffer sizes
		this.iter = new PipedRDFIterator<Triple>();
		final PipedRDFStream<Triple> tripleStream = new PipedTriplesStream(this.iter);
		final TypedInputStream tis = source.asTypedInputStream();
		// PipedRDFStream and PipedRDFIterator need to be on different threads
		this.executor = Executors.newSingleThreadExecutor();

		// Create a runnable for our parser thread
		Runnable parser = new Runnable() {

			@Override
			public void run() {
				// Call the parsing process.
				System.out.println("started thread before");
				RDFDataMgr.parse(tripleStream, tis);
				System.out.println("started thread after");
			}
		};

		// Start the parser on another thread
		this.executor.execute(parser);
		return AQuiXEvent.getStartRDF();
	}

	@Override
	protected AQuiXEvent process(CallBack callback) throws QuiXException {
		// We will consume the input on the main thread here
		// System.out.println("process");
		try {
			if (!this.buffer.isEmpty()) {
				return this.buffer.poll();
			}
			AQuiXEvent event = null;
			if (!this.iter.hasNext() && callback.getState() == State.START_SOURCE) {
				// special case if the buffer is empty but the document has not
				// been closed
				event = AQuiXEvent.getEndRDF();
				callback.setState(State.END_SOURCE);
				return event;
			}
			if (callback.getState() == State.END_SOURCE) {
				return callback.processEndSource();
			}
			// this iter has next
			Triple next = this.iter.next();

			this.buffer.add(AQuiXEvent.getEndPredicate(QuiXCharStream.fromSequence(next.toString())));
			return AQuiXEvent.getStartPredicate(QuiXCharStream.fromSequence(next.toString()));
		} catch (Exception e) {
			throw new QuiXException(e);
		}
	}

	@Override
	public void reinitialize(AStreamSource current) {
		//
		this.buffer.clear();
	}

	@Override
	public void close() {
		this.executor.shutdownNow();
	}

}
