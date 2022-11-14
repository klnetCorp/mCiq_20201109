package mciq2.klnet.co.kr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class PushActivity extends Activity {

    String push_seq = null;
    String push_msg = null;
    String push_obj_id = null;
    String push_sub_obj_id = null;
    String push_recv_id = null;
    String push_type = null;
    String push_isbackground = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataSet.getInstance().isrunapppush = true;
        setContentView(R.layout.activity_push);
        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.d("CHECK", String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }
            }

            Log.i("CHECK", "----- userid : "+getIntent().getExtras().getString("userid"));


            String msg = getIntent().getExtras().getString("msg");
            Log.i("CHECK", "msg : "+msg);

            JSONObject data = null;
            try {
                data = new JSONObject(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (data != null) {
                try {

                    DataSet.getInstance().push_id = data.getString("seq");
                    DataSet.getInstance().msg = getIntent().getExtras().getString("alert");
                    DataSet.getInstance().recv_id = getIntent().getExtras().getString("userid");

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("push_id", DataSet.getInstance().push_id);
                    intent.putExtra("msg", DataSet.getInstance().msg);
                    intent.putExtra("recv_id", DataSet.getInstance().recv_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    DataSet.getInstance().isbackground = "true";

                    if(DataSet.getInstance().islogin.equals("true")) {
                        finish();
                    } else {
                        this.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        finish();
    }


}
