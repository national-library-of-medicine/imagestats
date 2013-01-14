CREATE TABLE `imagestats` (
  `image_id` bigint(20) NOT NULL,
  `gt_initial_person` varchar(32) DEFAULT NULL,
  `gt_final_person` varchar(32) DEFAULT NULL,
  `initial_regions` varchar(512) DEFAULT NULL,
  `final_regions` varchar(512) DEFAULT NULL,
  `initial_updated_time` datetime DEFAULT NULL,
  `final_updated_time` datetime DEFAULT NULL,
  `groundTruthStatus` smallint(6) NOT NULL DEFAULT '0',
  `record_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`image_id`),
) DEFAULT CHARSET=utf8
