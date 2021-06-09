package team.one.lwe.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import team.one.lwe.R;
import team.one.lwe.bean.RoomType;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder> {
    private final List<RoomType> typeList;
    private final Context context;

    public RoomTypeAdapter(List<RoomType> typeList, Context context) {
        this.typeList = typeList;
        this.context = context;
    }

    @Override
    public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roomtype, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RoomType roomtype = typeList.get(position);
        holder.textType.setText(roomtype.getType());
        RoomAdapter RoomAdapter = new RoomAdapter(roomtype.getRoomIds());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.listRooms.setLayoutManager(gridLayoutManager);
        holder.listRooms.setAdapter(RoomAdapter);
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textType;
        private final RecyclerView listRooms;

        public ViewHolder(View view) {
            super(view);
            textType = view.findViewById(R.id.textType);
            listRooms = view.findViewById(R.id.listRooms);
        }
    }

}
