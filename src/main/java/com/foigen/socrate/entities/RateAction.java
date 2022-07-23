package com.foigen.socrate.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/*
    на каждом списании/начислении висит набор тегов,
    сама операция помимо значения, названия, имеет параметр isSample,
    указывающий на то, что данная операция является
    шаблонным нарушением/поощрением, подобные операции не требуют
    точной декларации и имеют заранее заданные пресеты.
    p.s. система модификаторов пока продумывается, но предполагается,
    что они должны работать не как суммарная накидка процентов со всех,
    а по какой-то более продуманной системе, чтобы избежать неадекватных
    штрафов из-за синергии пачки различных показателей
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class RateAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    Date date;
    @ManyToOne(fetch = FetchType.EAGER)
    User user;
    Integer rate;
    Boolean isTemplate;
    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    List<RateTag> tags=new ArrayList<>();

}
