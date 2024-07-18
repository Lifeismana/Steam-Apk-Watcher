package com.valvesoftware.android.steam.community.fragment;

import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GenericFilter<T> extends Filter {
    private final FilterableAdapter associatedAdapter;
    private final List<T> originalList;
    private final T searchItem;

    public GenericFilter(List<T> list, FilterableAdapter filterableAdapter, T t) {
        this.searchItem = t;
        this.originalList = new ArrayList(list);
        this.associatedAdapter = filterableAdapter;
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    @Override // android.widget.Filter
    protected Filter.FilterResults performFiltering(CharSequence charSequence) {
        String lowerCase = charSequence != null ? charSequence.toString().toLowerCase() : null;
        Filter.FilterResults filterResults = new Filter.FilterResults();
        if (lowerCase != null && lowerCase.length() > 0) {
            ArrayList arrayList = new ArrayList();
            for (T t : this.originalList) {
                if (t.toString().toLowerCase().contains(lowerCase) || t == this.searchItem) {
                    arrayList.add(t);
                }
            }
            filterResults.count = arrayList.size();
            filterResults.values = arrayList;
        } else {
            synchronized (this) {
                filterResults.values = this.originalList;
                filterResults.count = this.originalList.size();
            }
        }
        return filterResults;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.widget.Filter
    protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
        List list = (List) filterResults.values;
        if (list == null) {
            return;
        }
        this.associatedAdapter.notifyDataSetChanged();
        this.associatedAdapter.clear();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            this.associatedAdapter.add(list.get(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addToOriginal(T t) {
        this.originalList.add(t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeFromOriginal(T t) {
        this.originalList.remove(t);
    }
}
