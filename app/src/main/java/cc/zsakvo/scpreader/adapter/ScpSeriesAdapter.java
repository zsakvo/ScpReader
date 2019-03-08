package cc.zsakvo.scpreader.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.scpreader.R;

public class ScpSeriesAdapter extends RecyclerView.Adapter<ScpSeriesAdapter.ViewHolder> implements View.OnClickListener{

    private List<String> scpSn = new ArrayList<>();
    private List<String> scpContentUrl = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public ScpSeriesAdapter(List<String> scpSn, List<String> scpContentUrl){

        this.scpSn = scpSn;
        this.scpContentUrl = scpContentUrl;
    }


    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_scp_series, null);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.scpSn.setText(scpSn.get(position));
//        holder.scpName.setText(scpName.get(position));
        holder.itemView.setTag(position);
    }
    @Override
    public int getItemCount() {
        return  scpSn == null ? 0 : scpSn.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView scpSn;
        TextView scpName;
        ViewHolder(View itemView) {
            super(itemView);
            scpSn = (TextView) itemView.findViewById(R.id.scp_s_card_sn);
//            scpName = (TextView) itemView.findViewById(R.id.scp_s_card_name);
        }
    }
}
