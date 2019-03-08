package cc.zsakvo.scpreader.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.scpreader.R;

import static android.content.ContentValues.TAG;

public class ScpContentAdapter extends RecyclerView.Adapter<ScpContentAdapter.ViewHolder> implements View.OnClickListener {

    private List<String> scpStr = new ArrayList<>();
    private List<String> scpDesc = new ArrayList<>();
    private List<Integer> noneList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public ScpContentAdapter(List<String> scpStr, List<String> scpDesc, List<Integer> noneList) {

        this.scpStr = scpStr;
        this.scpDesc = scpDesc;
        this.noneList = noneList;
    }


    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_scp_content, null);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.scpStr.setText(scpStr.get(position));
        if (noneList.contains(position)) {
            holder.scpDec.setVisibility(View.GONE);
        } else {
            holder.scpDec.setVisibility(View.VISIBLE);
            holder.scpDec.setText(scpDesc.get(position));
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return scpStr == null ? 0 : scpStr.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView scpStr;
        TextView scpDec;

        ViewHolder(View itemView) {
            super(itemView);
            scpStr = (TextView) itemView.findViewById(R.id.scp_c_card_str);
            scpDec = (TextView) itemView.findViewById(R.id.scp_c_card_desc);
        }
    }
}
