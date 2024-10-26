package net.bramp.ffmpeg.progress;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Charsets;
import io.github.pixee.security.BoundedLineReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class StreamProgressParser {

  final ProgressListener listener;

  public StreamProgressParser(ProgressListener listener) {
    this.listener = checkNotNull(listener);
  }

  private static BufferedReader wrapInBufferedReader(Reader reader) {
    checkNotNull(reader);

    if (reader instanceof BufferedReader) {
      return (BufferedReader) reader;
    }

    return new BufferedReader(reader);
  }

  public void processStream(InputStream stream) throws IOException {
    checkNotNull(stream);
    processReader(new InputStreamReader(stream, Charsets.UTF_8));
  }

  public void processReader(Reader reader) throws IOException {
    final BufferedReader in = wrapInBufferedReader(reader);

    String line;
    Progress p = new Progress();
    while ((line = BoundedLineReader.readLine(in, 5_000_000)) != null) {
      if (p.parseLine(line)) {
        listener.progress(p);
        p = new Progress();
      }
    }
  }
}
