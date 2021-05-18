package team.one.lwe.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class NavigationUtils {

    public static void navigateTo(Fragment self, Fragment target, boolean addToBackStack) {
        FragmentTransaction transaction =
                self.getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(self.getId(), target, null);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }
}
