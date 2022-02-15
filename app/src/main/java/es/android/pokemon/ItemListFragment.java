package es.android.pokemon;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import es.android.pokemon.databinding.FragmentItemListBinding;
import es.android.pokemon.databinding.ItemListContentBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

/**
 * A fragment representing a list of Items. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListFragment extends Fragment {


    private FragmentItemListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.itemList;

        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_fragment);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://pokeapi.co/api/v2/").addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create())).build();

        PokemonAPIService pokemonApiService = retrofit.create(PokemonAPIService.class);

        Call<PokemonFetchResults> call = pokemonApiService.getPokemons();
        call.enqueue(new Callback<PokemonFetchResults>() {
            @Override
            public void onResponse(Call<PokemonFetchResults> call, Response<PokemonFetchResults> response) {
                if (response.isSuccessful()) {
                    List<Pokemon> pokemonList = response.body().getResults();
                    View recyclerView = getActivity().findViewById(R.id.item_list);
                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView, itemDetailFragmentContainer, pokemonList);
                } else {
                    Log.d("Error", "Something happened");
                    return;
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });

    }

    private void setupRecyclerView(RecyclerView recyclerView, View itemDetailFragmentContainer, List<Pokemon> items) {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(items));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Pokemon> mValues;

        SimpleItemRecyclerViewAdapter(List<Pokemon> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ItemListContentBinding binding = ItemListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(Integer.toString(position));
            holder.mContentView.setText(mValues.get(position).getName());
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickListener);
        }
        private final View.OnClickListener mOnClickListener = new
            View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = (int) view.getTag();
                    Pokemon item = mValues.get(index);
                    Bundle arguments = new Bundle();
                    arguments.putInt(String.valueOf(ItemDetailFragment.ARG_ITEM_ID), index + 1);
                    arguments.putString(ItemDetailFragment.ARG_ITEM_NAME, item.getName());
                    arguments.putString(ItemDetailFragment.ARG_DESCRIPTION,item.getDescription());
                    Navigation.findNavController(view).navigate(R.id.show_item_detail, arguments);
                }
            };

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(ItemListContentBinding binding) {
                super(binding.getRoot());
                mIdView = binding.idText;
                mContentView = binding.content;
            }
        }
    }
}