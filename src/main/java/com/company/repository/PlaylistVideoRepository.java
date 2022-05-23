package com.company.repository;

import com.company.entity.PlaylistVideoEntity;
import com.company.enums.VideoStatus;
import com.company.mapper.GetTop2VideoInfoMapper;
import com.company.mapper.PlaylistVideoInfoMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideoEntity, UUID> {

    Optional<PlaylistVideoEntity> findByPlaylistIdAndVideoId(UUID playlistId, UUID videoId);

    @Query(value = """
                    select cast(pv.id as varchar)         as pv_id,
                    pv.order_num    as pv_order_num,
                    pv.created_date as pv_created_date,
                    cast(pl.id as varchar)          as pl_id,
            cast(v.id  as varchar)          as v_id,
            v.title        as v_title,
            cast(v.preview_photo_id as varchar) as v_preview_photo,
            v.duration     as v_duration,
            cast(ch.id as varchar)          as ch_id,
            ch.name        as ch_name,
            cast(ch.photo_id as varchar)     as ch_photo
            from playlist_video as pv
            inner join video as v on pv.video_id = v.id
            inner join playlist as pl on pv.playlist_id = pl.id
            inner join channel as ch on v.channel_id = ch.id
                    where cast(pl.id as varchar) = :playlistId 
                    and v.status = :status 
                    and v.visible = true 
                    order by pv.order_num asc
                    """, nativeQuery = true)
    List<PlaylistVideoInfoMapper> findAllByPlaylistId(@Param("playlistId") String playlistId, @Param("status") String status);

    Optional<PlaylistVideoEntity> findByVideoId(UUID videoId);

    @Query("select count(id) from PlaylistVideoEntity where playlistId = :playlistId")
    int getVideoCountByPlaylistId(@Param("playlistId") UUID playlistId);

    @Query(value = "select cast(v.id as varchar), v.title, v.duration from playlist_video pv " +
            "inner join video v on v.id = pv.video_id " +
            "inner join playlist p on p.id = pv.playlist_id " +
            "where pv.playlist_id = :playlistId " +
            "and v.status = 'PUBLIC' " +
            "and p.visible = true " +
            "order by p.order_num limit 2"
            , nativeQuery = true)
    List<GetTop2VideoInfoMapper> getTop2VideoByPlaylistId(@Param("playlistId") UUID playlistId);


    @Query(value = """
                    select cast(pv.id as varchar)as pv_id,
                    pv.order_num as pv_order_num,
                    pv.created_date as pv_created_date,
                    cast(pl.id as varchar)
            as pl_id,

            cast(v.id as varchar)          as v_id,

            v.title as
            v_title,

            cast(v.preview_photo_id as varchar) as v_preview_photo,

            v.duration as
            v_duration,

            cast(ch.id as varchar)          as ch_id,

            ch.name as
            ch_name,

            cast(ch.photo_id as varchar)     as ch_photo

            from playlist_video
            as pv
            inner join
            video as
            v on
            pv.video_id =v.id
            inner join
            playlist as
            pl on
            pv.playlist_id =pl.id
            inner join
            channel as
            ch on
            v.channel_id =ch.id
            where pv.id =:playlistId""", nativeQuery = true)
    Optional<PlaylistVideoInfoMapper> findByIdMapper(@Param("playlistId") UUID playlistId);
}