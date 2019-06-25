package view.gui;

import javafx.geometry.Insets;
import model.map.GameMap;

import java.util.List;

class MapInsetsHelper {
    private static final List<List<Insets>> ammotileInsetsA1 = List.of(
            List.of(new Insets(203.5, 0, 0, 379.0),
                    new Insets(273.5, 0, 0, 520.0)),
            List.of(new Insets(0, 0, 0, 0),
                    new Insets(389.5, 0, 0, 507.0)),
            List.of(new Insets(0, 0, 0, 0),
                    new Insets(570.5, 0, 0, 512.0)));

    private static final List<List<Insets>> ammotileInsetsB1 = List.of(
            List.of(new Insets(278.5, 0, 0, 382.0),
                    new Insets(198.5, 0, 0, 520.0)),
            List.of(new Insets(0, 0, 0, 0),
                    new Insets(394.5, 0, 0, 507.0)),
            List.of(new Insets(570.5, 0, 0, 384.0),
                    new Insets(570.5, 0, 0, 512.0)));

    private static final List<List<Insets>> ammotileInsetsA2 = List.of(
            List.of(new Insets(0, 0, 0, 0),
                    new Insets(0, 0, 0, 0)),
            List.of(new Insets(409.5, 0, 0, 688.0),
                    new Insets(408.5, 0, 0, 896.0)),
            List.of(new Insets(570.5, 0, 0, 670.0),
                    new Insets(0, 0, 0, 0)));

    private static final List<List<Insets>> ammotileInsetsB2 = List.of(
            List.of(new Insets(0, 0, 0, 0),
                    new Insets(271.5, 0, 0, 896.0)),
            List.of(new Insets(437.5, 0, 0, 686.0),
                    new Insets(436.5, 0, 0, 823.0)),
            List.of(new Insets(566.5, 0, 0, 695.0),
                    new Insets(0, 0, 0, 0)));

    private static final List<List<List<Insets>>> playerInsetsA1 = List.of(
            List.of(
                    List.of(new Insets(190.5, 0, 0, 433.0),
                            new Insets(236.5, 0, 0, 375.0),
                            new Insets(230.5, 0, 0, 433.0),
                            new Insets(276.5, 0, 0, 375.0),
                            new Insets(270.5, 0, 0, 433.0)),
                    List.of(new Insets(191.5, 0, 0, 517.0),
                            new Insets(191.5, 0, 0, 591.0),
                            new Insets(231.5, 0, 0, 517.0),
                            new Insets(231.5, 0, 0, 591.0),
                            new Insets(271.5, 0, 0, 591.0))),
            List.of(
                    List.of(new Insets(348.5, 0, 0, 364.0),
                            new Insets(348.5, 0, 0, 424.0),
                            new Insets(388.5, 0, 0, 364.0),
                            new Insets(388.5, 0, 0, 424.0),
                            new Insets(428.5, 0, 0, 394.0)),
                    List.of(new Insets(343.5, 0, 0, 546.0),
                            new Insets(343.5, 0, 0, 595.0),
                            new Insets(383.5, 0, 0, 546.0),
                            new Insets(383.5, 0, 0, 595.0),
                            new Insets(423.5, 0, 0, 569.0))),
            List.of(
                    List.of(new Insets(0, 0, 0, 0),
                            new Insets(0, 0, 0, 0),
                            new Insets(0, 0, 0, 0),
                            new Insets(0, 0, 0, 0),
                            new Insets(0, 0, 0, 0)),
                    List.of(new Insets(519.5, 0, 0, 508.0),
                            new Insets(519.5, 0, 0, 556.0),
                            new Insets(519.5, 0, 0, 603.0),
                            new Insets(568.5, 0, 0, 556.0),
                            new Insets(568.5, 0, 0, 603.0))));

    private static final List<List<List<Insets>>> playerInsetsB1 = List.of(
            List.of(
                    List.of(new Insets(192.5, 0, 0, 360.0),
                            new Insets(192.5, 0, 0, 430.0),
                            new Insets(234.5, 0, 0, 360.0),
                            new Insets(234.5, 0, 0, 430.0),
                            new Insets(277.5, 0, 0, 430.0)),
                    List.of(new Insets(194.5, 0, 0, 558.0),
                            new Insets(194.5, 0, 0, 601.0),
                            new Insets(256.5, 0, 0, 513.0),
                            new Insets(256.5, 0, 0, 558.0),
                            new Insets(256.5, 0, 0, 601.0))),
            List.of(
                    List.of(new Insets(345.5, 0, 0, 356.0),
                            new Insets(345.5, 0, 0, 426.0),
                            new Insets(387.5, 0, 0, 356.0),
                            new Insets(387.5, 0, 0, 426.0),
                            new Insets(430.5, 0, 0, 356.0)),
                    List.of(new Insets(348.5, 0, 0, 508.0),
                            new Insets(348.5, 0, 0, 548.0),
                            new Insets(348.5, 0, 0, 588.0),
                            new Insets(388.5, 0, 0, 588.0),
                            new Insets(428.5, 0, 0, 588.0))),
            List.of(
                    List.of(new Insets(512.5, 0, 0, 363.0),
                            new Insets(512.5, 0, 0, 403.0),
                            new Insets(512.5, 0, 0, 443.0),
                            new Insets(552.5, 0, 0, 443.0),
                            new Insets(592.5, 0, 0, 443.0)),
                    List.of(new Insets(512.5, 0, 0, 507.0),
                            new Insets(512.5, 0, 0, 547.0),
                            new Insets(512.5, 0, 0, 587.0),
                            new Insets(552.5, 0, 0, 587.0),
                            new Insets(592.5, 0, 0, 587.0))));

    private static final List<List<List<Insets>>> playerInsetsA2 = List.of(
            List.of(
                    List.of(new Insets(191.5, 0, 0, 679.0),
                            new Insets(231.5, 0, 0, 679.0),
                            new Insets(231.5, 0, 0, 741.0),
                            new Insets(271.5, 0, 0, 679.0),
                            new Insets(271.5, 0, 0, 741.0)),
                    List.of(new Insets(0, 0, 0, 0),
                            new Insets(0, 0, 0, 0),
                            new Insets(0, 0, 0, 0),
                            new Insets(0, 0, 0, 0),
                            new Insets(0, 0, 0, 0))),
            List.of(
                    List.of(new Insets(345.5, 0, 0, 662.0),
                            new Insets(345.5, 0, 0, 705.0),
                            new Insets(345.5, 0, 0, 749.0),
                            new Insets(388.5, 0, 0, 749.0),
                            new Insets(431.5, 0, 0, 749.0)),
                    List.of(new Insets(350.5, 0, 0, 839.0),
                            new Insets(350.5, 0, 0, 893.0),
                            new Insets(397.5, 0, 0, 839.0),
                            new Insets(444.5, 0, 0, 839.0),
                            new Insets(444.5, 0, 0, 893.0))),
            List.of(
                    List.of(new Insets(519.5, 0, 0, 661.0),
                            new Insets(519.5, 0, 0, 709.0),
                            new Insets(519.5, 0, 0, 756.0),
                            new Insets(568.5, 0, 0, 709.0),
                            new Insets(568.5, 0, 0, 756.0)),
                    List.of(new Insets(485.5, 0, 0, 835.0),
                            new Insets(485.5, 0, 0, 892.0),
                            new Insets(535.5, 0, 0, 835.0),
                            new Insets(587.5, 0, 0, 835.0),
                            new Insets(587.5, 0, 0, 892.0))));

    private static final List<List<List<Insets>>> playerInsetsB2 = List.of(
            List.of(
                    List.of(new Insets(188.5, 0, 0, 664.0),
                            new Insets(188.5, 0, 0, 734.0),
                            new Insets(230.5, 0, 0, 664.0),
                            new Insets(230.5, 0, 0, 734.0),
                            new Insets(273.5, 0, 0, 664.0)),
                    List.of(new Insets(182.5, 0, 0, 833.0),
                            new Insets(182.5, 0, 0, 903.0),
                            new Insets(224.5, 0, 0, 833.0),
                            new Insets(224.5, 0, 0, 903.0),
                            new Insets(267.5, 0, 0, 833.0))),
            List.of(
                    List.of(new Insets(346.5, 0, 0, 681.0),
                            new Insets(346.5, 0, 0, 751.0),
                            new Insets(388.5, 0, 0, 681.0),
                            new Insets(388.5, 0, 0, 751.0),
                            new Insets(436.5, 0, 0, 751.0)),
                    List.of(new Insets(346.5, 0, 0, 818.0),
                            new Insets(346.5, 0, 0, 888.0),
                            new Insets(388.5, 0, 0, 818.0),
                            new Insets(388.5, 0, 0, 888.0),
                            new Insets(436.5, 0, 0, 888.0))),
            List.of(
                    List.of(new Insets(512.5, 0, 0, 672.0),
                            new Insets(512.5, 0, 0, 712.0),
                            new Insets(512.5, 0, 0, 752.0),
                            new Insets(552.5, 0, 0, 752.0),
                            new Insets(592.5, 0, 0, 752.0)),
                    List.of(new Insets(485.5, 0, 0, 835.0),
                            new Insets(485.5, 0, 0, 892.0),
                            new Insets(535.5, 0, 0, 835.0),
                            new Insets(587.5, 0, 0, 835.0),
                            new Insets(587.5, 0, 0, 892.0))));

    static final Insets playerBoardInsets = new Insets(0, 0, 0, 310);

    static final Insets startingFirstAmmoInsets = new Insets(28, 0, 0, 865);
    static final Insets startingSecondAmmoInsets = new Insets(68, 0, 0, 865);
    static final Insets startingThirdAmmoInsets = new Insets(108, 0, 0, 865);
    static final double AMMO_HORIZONTAL_OFFSET = 36.0;

    static final Insets startingDamageInsets = new Insets(70, 0, 0, 375);
    static final double DAMAGE_HORIZONTAL_OFFSET = 38.7;

    static final Insets startingMarksInsets = new Insets(2, 0, 0, 635);
    static final double MARKS_HORIZONTAL_OFFSET = 20.0;

    static final Insets weaponHBoxInsets = new Insets(200, 0, 0, 0);
    static final Insets powerupsHBoxInsets = new Insets(464, 0, 0, 0);

    static Insets getAmmoTileInsets(int map, int x, int y) {
        if (x < 0 || x >= GameMap.MAX_ROWS || y < 0 || y >= GameMap.MAX_COLUMNS || map < 1 || map > 4) {
            throw new IndexOutOfBoundsException();
        }

        List<List<Insets>> insetsList;

        switch (map) {
            case 1:
                insetsList = (y < 2) ? ammotileInsetsA1 : ammotileInsetsA2;
                break;
            case 2:
                insetsList = (y < 2) ? ammotileInsetsA1 : ammotileInsetsB2;
                break;
            case 3:
                insetsList = (y < 2) ? ammotileInsetsB1 : ammotileInsetsB2;
                break;
            case 4:
                insetsList = (y < 2) ? ammotileInsetsB1 : ammotileInsetsA2;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        if (y >= 2) {
            y = y - 2;
        }

        return insetsList.get(x).get(y);
    }

    static Insets getPlayerInsets(int map, int x, int y, int numPlayer) {
        if (x < 0 || x >= GameMap.MAX_ROWS || y < 0 || y >= GameMap.MAX_COLUMNS
                || map < 1 || map > 4 || numPlayer < 0 || numPlayer > 4) {
            throw new IndexOutOfBoundsException();
        }

        List<List<List<Insets>>> insetsList;

        switch (map) {
            case 4:
                insetsList = (y < 2) ? playerInsetsB1 : playerInsetsA2;
                break;
            case 3:
                insetsList = (y < 2) ? playerInsetsB1 : playerInsetsB2;
                break;
            case 2:
                insetsList = (y < 2) ? playerInsetsA1 : playerInsetsB2;
                break;
            case 1:
                insetsList = (y < 2) ? playerInsetsA1 : playerInsetsA2;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        if (y >= 2) {
            y = y - 2;
        }

        return insetsList.get(x).get(y).get(numPlayer);
    }

    private MapInsetsHelper() {
        throw new IllegalStateException("Utility Class");
    }
}
