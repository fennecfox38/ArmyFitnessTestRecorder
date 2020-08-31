package army.prt.recorder.abcp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import army.prt.recorder.R;
import army.prt.recorder.databinding.RecyclerviewItemAbcpBinding;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemViewHolder> {
    private Context context;
    public MutableLiveData<ArrayList<Item>> itemList;

    public ItemRecyclerAdapter(Context context, MutableLiveData<ArrayList<Item>> itemList){
        this.context = context;
        this.itemList = itemList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private RecyclerviewItemAbcpBinding binding;
        public Item item = null; // It will be assigned in 'onBindViewHolder'
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        /*public void afterTextChanged(Editable s) {
            String string = s.toString();
            if(string.length()==0) return;
            try{
                switch (item.itemType){
                    case Item.HEIGHT: updateRaw((int)((Float.parseFloat(string)-58)*2)); break;
                    case Item.WEIGHT: updateRaw(Integer.parseInt(string)-90); break;
                    case Item.NECK: updateRaw((int)((Float.parseFloat(string)-10)*2)); break;
                    case Item.WAIST: case Item.HIPS:
                        updateRaw((int)((Float.parseFloat(string)-20)*2)); break;
                }
            }catch(Exception e){ e.printStackTrace(); }
        }*/
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) updateRaw(progress);
        }
        public void onAdjustBtnClick(View v) {
            switch (v.getId()){
                case R.id.btn_minus: updateRaw(item.raw-1); break;
                case R.id.btn_plus: updateRaw(item.raw+1); break;
            }
        }
        private void updateRaw(int rawSco){
            // 범위 문제 edittext때문에 좀더 고민해 봐야함. 수정 중 범위벗어나는 걸 허락 하기가 어려움.
            if(rawSco == item.raw) return;
            else if(rawSco<item.min) rawSco = item.min;
            else if(rawSco>item.max) rawSco = item.max;
            item.raw = rawSco;
            updateItemList(item.itemType,item);
            binding.invalidateAll();
        }
    }

    @NonNull @Override public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (new ItemViewHolder(inflater.inflate(R.layout.recyclerview_item_abcp,parent,false)));
    }

    @Override public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.binding.setViewholder(holder);
        holder.item = itemList.getValue().get(position);
    }

    @Override public int getItemCount() { return itemList.getValue().size(); }

    private void updateItemList(int position, Item item){
        ArrayList<Item> list = itemList.getValue();
        list.set(position, item);
        itemList.setValue(list);
    }

    @BindingAdapter("android:text")
    public static void setText(EditText editText, Item item) {
        switch (item.itemType){
            case Item.HEIGHT:
                //try{ if(item.raw == (int)(((Float.parseFloat(editText.getText().toString()))-58) *2)) return; }
                //catch (Exception e){ e.printStackTrace(); }\
                editText.setText(String.valueOf(58.f + item.raw/2.f)); break;
            case Item.WEIGHT:
                //try{ if(item.raw == Integer.parseInt(editText.getText().toString())-90) return; }
                //catch (Exception e){ e.printStackTrace(); }\
                editText.setText(String.valueOf(90 + item.raw)); break;
            case Item.NECK:
                //try{ if(item.raw == (int)(((Float.parseFloat(editText.getText().toString()))-10) *2)) return; }
                //catch (Exception e){ e.printStackTrace(); }\
                editText.setText(String.valueOf(10.f + item.raw/2.f)); break;
            case Item.ABDOMEN_WAIST: case Item.HIPS:
                //try{ if(item.raw == (int)(((Float.parseFloat(editText.getText().toString()))-20) *2)) return; }
                //catch (Exception e){ e.printStackTrace(); }\
                editText.setText(String.valueOf(20.f + item.raw/2.f)); break;
        }
        editText.setSelection(editText.length());
    }

}
