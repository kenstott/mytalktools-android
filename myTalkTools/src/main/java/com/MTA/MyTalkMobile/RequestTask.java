package com.MTA.MyTalkMobile;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * The Class RequestTask.
 *
 * @author caseymattingly
 */
class RequestTask extends AsyncTask<String, String, String> {

    private final WeakReference<Board> board;
    private String resultOfProcess;

    RequestTask(Board board) {
        this.board = new WeakReference<>(board);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(final String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
                resultOfProcess = responseString;
                Log.d("result: ", responseString);
            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return responseString;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        Log.d("Result String: ", resultOfProcess);
        if (resultOfProcess.contains("true")) {
            Toast.makeText(board.get(), R.string.workspace_is_activated_, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(board.get(), R.string.there_was_a_problem, Toast.LENGTH_SHORT).show();
        }
    }
}
