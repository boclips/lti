import React, { useState } from 'react';
import { Col, Collapse, Row } from 'antd';

import LicensedBadge from '../../resources/images/licensed-badge.svg';
import YoutubeBadge from '../../resources/images/youtube-badge.svg';
import ContentWarningBadge from '../../resources/images/content-warning-badge.svg';

import SearchIcon from '../../resources/images/search.svg';
import SubtitleIcon from '../../resources/images/subtitle.svg';
import VideoEditIcon from '../../resources/images/video-edit.svg';

import ArrowUp from '../../resources/images/arrow-up.svg';
import ArrowDown from '../../resources/images/arrow-down.svg';

import s from './style.module.less';

const { Panel } = Collapse;

interface AdditionalServicesProps {
  title: string,
  id: string;
  children: React.ReactNode
  icon: React.ReactNode
}
const AdditionalServices = ({
  title, children, icon, id
}: AdditionalServicesProps) => {
  const [open, setOpen] = useState(false);
  return (
    <Collapse ghost onChange={() => setOpen(!open)} className={s.collapse}>
      <Panel showArrow={false} key={id} header={
        <Row>
          <Col className={s.serviceIcon}><div>{icon}</div></Col>
          <Col className={s.serviceTitle}><h3>{title}</h3> {open ? <ArrowUp/> : <ArrowDown/>}</Col>
        </Row>
      }
      >
        {children}
      </Panel>
    </Collapse>
  );
};

interface DetailsWithIconProps {
  description: React.ReactNode,
  icon: React.ReactNode
}
const DetailsWithIcon = ({ description, icon }: DetailsWithIconProps) => (
  <Row className={s.row}>
    <Col xs={6} className={s.badge}>{icon}</Col>
    <Col xs={18}><p>{description}</p></Col>
  </Row>
);

interface SlsFormLinkProps {
  text: string;
}
const SlsFormLink = ({ text }: SlsFormLinkProps) => <b><a href="https://forms.gle/48kwZbr8bvHfNST2A" target="_blank" rel="noopener noreferrer" className={s.link}>{text}</a></b>;

const AboutAppAndServicesDetails = () => (<div className={s.details}>
  <h1>How does it work?</h1>
  <p>
    <b>Video Library</b>, powered by <b>BoClips</b>, gives you access to a comprehensive library
    of licensed resources to use in SLS lessons. BoClips represents over 200 of the world’s
    best educational video brands, covering all MOE syllabus subjects and age levels.
  </p>
  <h2>How does licensing work?</h2>
  <DetailsWithIcon
    description={
      <>
      Licensed videos from BoClips are clearly marked with a label next to each video, so you know
      the brand is trusted and the copyright is cleared.
      </>
    } 
    icon={<LicensedBadge/>}
  />
  <DetailsWithIcon
    description={
      <>
        In addition to BoClips licensed content, you’re also able to search for YouTube videos in
        the Video Library.
        Those videos, not licensed by BoClips, are clearly marked with the YouTube label.
        <b> These resources may include advertising and 3rd party content.</b>
      </>
    }
    icon={<YoutubeBadge/>}
  />
  <h2>Content warnings</h2>
  <DetailsWithIcon
    description={
      <>
        Potentially challenging content such as videos dealing with racism, homophobia, sexism, and
        violence is clearly marked for any user who wishes to know about it in advance.
        Hover over the content warning tag to check detailed information about the specific video.
      </>
    }
    icon={<ContentWarningBadge/>}
  />
  <h2>Additional services for MOE employees</h2>
  <AdditionalServices title="Request video research support" icon={<SearchIcon/>} id="research"
  >
    <p>
      <b>
        Research support and concierge service is provided as a core service in all subscription
        packages.
      </b>
    </p>
    <p>
      This service is designed to help you discover resources on the app and will also provide
      support for your video-focused projects large and small.
      Simply <SlsFormLink text="fill out this form"/> and our research team will source and select a list of recommended videos based on your
      learning outcomes, age level, and stylistic preferences. Our research experts have rich
      experience across different educational fields. The team will typically aim to respond to
      requests within 5 working days.
    </p>
  </AdditionalServices>
  <AdditionalServices title="Request subtitles" icon={<SubtitleIcon/>} id="subtitles">
    <p>Service cost: <b>$10 per video</b></p>
    <p>
      Upon request of subtitling a video, we’ll generate a human made audio transcript and add
      these subtitles to the video. <SlsFormLink text="Fill out this form"/> to request subtitles for the selected video.
      This process will be completed within 48 hours of a request and we’ll inform you upon
      completion so you can review the video on the app with subtitles. Once a video is embedded in
      a published lesson, students will be able to toggle them on and off. Any errors reported in
      generated subtitles will be remedied for no additional charge.
    </p>
  </AdditionalServices>
  <AdditionalServices title="Edit selected video" icon={<VideoEditIcon/>} id="edit">
    <p>
      Service cost: <b>$350 per video</b></p>
    <p>
      Our team can help you modify the selected video to achieve the perfect fit with lesson topics
      and the Singapore curriculum. <SlsFormLink text="Fill out this form"/> to describe specific instructions for modification.
      The team will typically try to respond to the request within 10 working days,
      depending on the scale of the project, but will otherwise inform you if more time is required.
    </p>
    <p>
      One feedback loop is permitted as part of this process, meaning you can ask for one round or
      further revision or corrections to this work after the first submission. Further editing
      requests for the same video submitted more than one month after initial edits have been
      completed will be treated as a separate editing project and is subject to a repeat charge.
    </p>
    <p>
      Edited videos will be delivered offline (not on the app) in High Definition MP4 format, or as
      otherwise requested and should be uploaded onto SLS platform by MOE.
      Edited videos are licensed for the duration of the subscription purchased by the relevant
      brand and represent a single video embed as part of the subscription package.
    </p>
    <p><b>What’s possible?</b></p>
    <p>
      Editing requests can include modification of voice-over/narration, trimming and sectioning of
      the video, modification of in-video text, and graphics. Editing request may not include the
      reshooting or animation of any additional scenes not already present in the resource. The
      addition or replacement of music backing tracks is possible, but all music backing tracks will
      be selected at Boclips’ discretion from a list of pre-licensed tracks based on the brief
      provided.
    </p>
  </AdditionalServices>
    
</div>);

export default AboutAppAndServicesDetails;
