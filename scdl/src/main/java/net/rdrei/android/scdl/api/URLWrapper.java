package net.rdrei.android.scdl.api;

import java.io.IOException;
import java.net.URLConnection;

public interface URLWrapper {

	URLConnection openConnection() throws IOException;

}