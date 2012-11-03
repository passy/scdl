package net.rdrei.android.scdl2.test;

import java.io.IOException;
import java.util.Map;

import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Transaction;

public class TrackerStub implements Tracker {

	@Override
	public boolean anonymizeIpEnabled() {
		return false;
	}

	@Override
	public void close() {

	}

	@Override
	public Map<String, String> constructEvent(String arg0, String arg1,
			String arg2, Long arg3) {
		return null;
	}

	@Override
	public Map<String, String> constructException(String arg0, boolean arg1) {
		return null;
	}

	@Override
	public Map<String, String> constructRawException(String arg0,
			Throwable arg1, boolean arg2) throws IOException {
		return null;
	}

	@Override
	public Map<String, String> constructSocial(String arg0, String arg1,
			String arg2) {
		return null;
	}

	@Override
	public Map<String, String> constructTiming(String arg0, long arg1,
			String arg2, String arg3) {
		return null;
	}

	@Override
	public Map<String, String> constructTransaction(Transaction arg0) {
		return null;
	}

	@Override
	public String get(String arg0) {
		return null;
	}

	@Override
	public String getAppId() {
		return null;
	}

	@Override
	public String getAppInstallerId() {
		return null;
	}

	@Override
	public ExceptionParser getExceptionParser() {
		return null;
	}

	@Override
	public double getSampleRate() {
		return 0;
	}

	@Override
	public String getTrackingId() {
		return null;
	}

	@Override
	public boolean getUseSecure() {
		return false;
	}

	@Override
	public void send(String arg0, Map<String, String> arg1) {

	}

	@Override
	public void set(String arg0, String arg1) {

	}

	@Override
	public void setAnonymizeIp(boolean arg0) {

	}

	@Override
	public void setAppId(String arg0) {

	}

	@Override
	public void setAppInstallerId(String arg0) {

	}

	@Override
	public void setAppName(String arg0) {

	}

	@Override
	public void setAppScreen(String arg0) {

	}

	@Override
	public void setAppVersion(String arg0) {

	}

	@Override
	public void setCampaign(String arg0) {

	}

	@Override
	public void setCustomDimension(int arg0, String arg1) {

	}

	@Override
	public void setCustomDimensionsAndMetrics(Map<Integer, String> arg0,
			Map<Integer, Long> arg1) {

	}

	@Override
	public void setCustomMetric(int arg0, Long arg1) {

	}

	@Override
	public void setExceptionParser(ExceptionParser arg0) {

	}

	@Override
	public void setReferrer(String arg0) {

	}

	@Override
	public void setSampleRate(double arg0) {

	}

	@Override
	public void setStartSession(boolean arg0) {

	}

	@Override
	public void setUseSecure(boolean arg0) {

	}

	@Override
	public void trackEvent(String arg0, String arg1, String arg2, Long arg3) {

	}

	@Override
	public void trackException(String arg0, boolean arg1) {

	}

	@Override
	public void trackException(String arg0, Throwable arg1, boolean arg2) {

	}

	@Override
	public void trackSocial(String arg0, String arg1, String arg2) {

	}

	@Override
	public void trackTiming(String arg0, long arg1, String arg2, String arg3) {

	}

	@Override
	public void trackTransaction(Transaction arg0) {

	}

	@Override
	public void trackView() {

	}

	@Override
	public void trackView(String arg0) {

	}

}
