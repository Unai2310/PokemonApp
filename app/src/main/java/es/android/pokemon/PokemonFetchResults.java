package es.android.pokemon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonFetchResults {
    @SerializedName("results")
    @Expose
    private List<Pokemon> results;
    public List<Pokemon> getResults() {
        return results;
    }
}
